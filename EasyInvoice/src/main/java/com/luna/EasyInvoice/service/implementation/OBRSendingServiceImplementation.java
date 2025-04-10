package com.luna.EasyInvoice.service.implementation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.luna.EasyInvoice.dto.ArticleDTOPojo;
import com.luna.EasyInvoice.dto.ConnexionPojo;
import com.luna.EasyInvoice.dto.InvoiceCancel;
import com.luna.EasyInvoice.dto.InvoiceDTOPojo;
import com.luna.EasyInvoice.dto.RequestDTO;
import com.luna.EasyInvoice.dto.ResponseBodyDTO;
import com.luna.EasyInvoice.dto.ResponseBodyDTOError;
import com.luna.EasyInvoice.dto.ResponseDTO;
import com.luna.EasyInvoice.dto.ResponsePojo;
import com.luna.EasyInvoice.dto.TinFirstResponse;
import com.luna.EasyInvoice.dto.TinRequest;
import com.luna.EasyInvoice.dto.TinResponse;
import com.luna.EasyInvoice.entities.ArticleInvoice;
import com.luna.EasyInvoice.entities.Interconnection;
import com.luna.EasyInvoice.entities.Invoice;
import com.luna.EasyInvoice.entities.Organisation;
import com.luna.EasyInvoice.service.OBRSendingService;
import com.luna.EasyInvoice.utility.JsonUtils;
import com.luna.EasyInvoice.utility.UtilitiesService;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

@Service
@Slf4j
public class OBRSendingServiceImplementation implements OBRSendingService {
	@Autowired
	UtilitiesService utilityservice;
	
	@Autowired
	InvoiceServiceImpl invoiceserviceimpl;
	
	@Override
	@Transactional
	public void sendInvoice(Invoice invoice, Organisation organisation, String API_URL, String username, String password) throws Exception {
		ResponsePojo rsp = this.getConnected(API_URL, username, password);
		InvoiceDTOPojo inv = new InvoiceDTOPojo();
		
		double ristourneRate = invoice.getRistourneRate();
		
		inv.setCancelledInvoice("");
		inv.setCt_taxpayer(0);
		inv.setCustomerAddress(invoice.getClient().getAddInfos());
		inv.setCustomerName(invoice.getClient().getName());
		if(invoice.getClient().isVatSubject()) {
			inv.setCustomerPayer(1);
		}else {
			inv.setCustomerPayer(0);
		}
		inv.setCustomerTin(invoice.getClient().getTIN());
		inv.setInvoiceCurrency(invoice.getCurrency());
		inv.setInvoiceDate(this.utilityservice.dateToString(invoice.getValidatedDate(),"yyyy-MM-dd HH:mm:ss"));
		inv.setPaymentType(invoice.getPaymentMode());
		inv.setReference(invoice.getValidationref());
		inv.setSignatureDate(this.utilityservice.dateToString(invoice.getValidatedDate(),"yyyy-MM-dd HH:mm:ss"));
		inv.setTl_taxpayer(0);
		inv.setTpActivitySector(organisation.getActivitySecter());
		inv.setTpAddressAvenue(organisation.getAddressAvenue());
		inv.setTpAddressCommune(organisation.getAddressCommune());
		inv.setTpAddressNumber(organisation.getAddressNumber());
		inv.setTpAddressProvince(organisation.getAddressProvince());
		inv.setTpAddressQuartier(organisation.getAddressQuartier());
		inv.setTpFiscalCenter(organisation.getFiscalCenter());
		inv.setTpLegalForm(organisation.getJuridictionForm());
		inv.setTpName(organisation.getName());
		inv.setTpPhoneNumber(organisation.getTelephone());
		inv.setTpPostalNumber(organisation.getPostalNumber());
		inv.setTpTin(organisation.getTin());
		inv.setTpTradeNumber(organisation.getTradeNumber());
		inv.setTpType("2");
		inv.setVatTaxPayer(1);
		List<ArticleDTOPojo> article = new ArrayList<>();
		for(ArticleInvoice ar : invoice.getArticles()) {
			ArticleDTOPojo art = new ArticleDTOPojo();
			art.setConsTax(0);
			art.setDesignation(ar.getTitle());
			art.setLevyTax(0);
			art.setUnity(ar.getUnity());
			
			double unityPrice = ar.getUnityPrice();
			double totalNV = ar.getTotalNVat();
			double quantity = totalNV/unityPrice;
			
			double taxRate = 0.0;
			taxRate = ar.getTax()/ar.getTotalNVat();
			
			if(ristourneRate>0) {
				unityPrice = unityPrice - (unityPrice*ristourneRate/100);
			}			
			
			totalNV = quantity*unityPrice;
			
			art.setQuantity(quantity);
			art.setNvatPrice(totalNV);
			art.setVat(totalNV*taxRate);
			
			
			art.setPrice(unityPrice);
			art.setTotalAmount(totalNV + art.getVat());			
			art.setWvatPrice(totalNV + art.getVat());
			
			article.add(art);
		}
		inv.setItems(article);
		inv.setInvoiceidentifier(invoice.getSignature());
		inv.setSignature(invoice.getSignature());
		
		ResponseBodyDTO resp = this.sendInvoice(inv, API_URL, rsp.getResult().getToken());
		invoice.setReponse("");
		
		String msg = "";
		String ebmCK= "";
		
		if(resp.isSuccess()) {
			msg = resp.getMessage();
			ebmCK= resp.getEbmsACK();
			invoice.setEbmsMsg(msg + "[OBR reg number : "+resp.getResult().getRegistrationNumber()+", recept date : "+resp.getResult().getRegistrationdate()+"]");
			invoice.setEbmsACK(ebmCK);
			invoice.setSignatureDate(invoice.getSignatureDate());
			invoice.setStatus(3);
		}else {
			msg = resp.getMessage();
			invoice.setEbmsMsg(msg);
			invoice.setEbmsACK("");
			invoice.setSignatureDate(invoice.getSignatureDate());
			invoice.setStatus(-3);
		}
		
		invoice = this.invoiceserviceimpl.save(invoice);
	}
	
	@Override
	public ResponseBodyDTO sendInvoice(InvoiceDTOPojo invoice, String API_URL, String token) throws Exception {
		//String RESSOURCE = "addInvoice";  
		String RESSOURCE = "addInvoice_confirm";
		String tok ="Bearer "+token;
		OkHttpClient client = new OkHttpClient().newBuilder().followRedirects(false).build();
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
	    builder.hostnameVerifier(new HostnameVerifier() {
	        @Override
	        public boolean verify(String hostname, SSLSession session) {
	            return true;
	        }
	    });
	   
    	MediaType mediaType = MediaType.parse("application/json");
    	RequestBody body = RequestBody.create(mediaType, JsonUtils.toJson(invoice));
    	System.out.println("To send 1 : "+JsonUtils.toJson(invoice).toString());
    	Request request = new Request.Builder()
    			.url(API_URL+RESSOURCE)
    			.method("POST", body)
			 	.addHeader("Authorization", tok)
			 	.addHeader("Content-Type", "application/json")
			 	.build();
    	Buffer buffer = new Buffer();
    	request.body().writeTo(buffer);
    	//System.out.println("To send : "+request.toString());
		Response response = client.newCall(request).execute();
		System.out.println("To receive : "+response.toString());
		String responseData = response.body().string();
		ResponseBodyDTO resp = JsonUtils.toObject(responseData, ResponseBodyDTO.class);
		
		try {
			resp = JsonUtils.toObject(responseData, ResponseBodyDTO.class);
		} catch (MismatchedInputException e) {
			ResponseBodyDTOError respError = JsonUtils.toObject(responseData, ResponseBodyDTOError.class);
			resp.setEbmsACK(respError.getEbmsACK());
			resp.setMessage(respError.getMessage());
			resp.setSuccess(respError.isSuccess());
		}
		
		return resp;
	}
	
	@Override
	public ResponsePojo getConnected(String API_URL, String username, String password) throws Exception {
		String ressource = "login";
		OkHttpClient client = new OkHttpClient().newBuilder().followRedirects(false).build();
		ConnexionPojo connexion = new ConnexionPojo();
		connexion.setUsername(username);
		connexion.setPassword(password);
    	OkHttpClient.Builder builder = new OkHttpClient.Builder();
	    builder.hostnameVerifier(new HostnameVerifier() {
	        @Override
	        public boolean verify(String hostname, SSLSession session) {
	            return true;
	        }
	    });
    	MediaType mediaType = MediaType.parse("application/json");
    	RequestBody body = RequestBody.create(mediaType, JsonUtils.toJson(connexion));
    	System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>.Connexion JSON TO send : "+JsonUtils.toJson(connexion));
    	System.out.println("Connexion JSON TO send 2 : "+body.toString());
    	Request request = new Request.Builder()
    			.url(API_URL+ressource)
    			.method("POST", body)
			 	.addHeader("Content-Type", "application/json")
			 	.build();
		Response response = client.newCall(request).execute();
		System.out.println("Connexion JSON TO request 3 : "+request.toString());
		System.out.println("Connexion JSON TO response 2 : "+response.toString());
		String responseData = response.body().string();
		ResponsePojo resp = JsonUtils.toObject(responseData, ResponsePojo.class);
		return resp;
	}
	@Transactional
	public void cancelInvoice(Invoice invoice, Organisation organisation) throws Exception {
		List<Interconnection> interco = organisation.getInterconnections();
		if(interco.size()>0) {
			log.info("In da service 111 ");
			InvoiceCancel cancel = new InvoiceCancel();
			cancel.setInvoiceSignature(invoice.getSignature());
			cancel.setCancelRaison(invoice.getCancelRaison());
			Interconnection inter = interco.get(0);
			ResponsePojo rsp = this.getConnected(inter.getUrl(), inter.getUsername(), inter.getPassword());
			String resp = this.cancelInvoice(cancel,  inter.getUrl(), rsp.getResult().getToken());
			invoice.setReponse(resp);
			log.info(resp);
		}
		invoice.setStatus(5);
		invoice = this.invoiceserviceimpl.save(invoice);
	}

	@Override
	public String cancelInvoice(InvoiceCancel invoice, String API_URL, String token) throws Exception {
		String RESSOURCE = "cancelInvoice";
		String tok ="Bearer "+token;
		OkHttpClient client = new OkHttpClient().newBuilder().followRedirects(false).build();
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
	    builder.hostnameVerifier(new HostnameVerifier() {
	        @Override
	        public boolean verify(String hostname, SSLSession session) {
	            return true;
	        }
	    });
	   
    	MediaType mediaType = MediaType.parse("application/json");
    	RequestBody body = RequestBody.create(mediaType, JsonUtils.toJson(invoice));
    	Request request = new Request.Builder()
    			.url(API_URL+RESSOURCE)
    			.method("POST", body)
			 	.addHeader("Authorization", tok)
			 	.addHeader("Content-Type", "application/json")
			 	.build();
    	Buffer buffer = new Buffer();
    	request.body().writeTo(buffer);
    	//System.out.println("To send : "+request.toString());
		Response response = client.newCall(request).execute();
		String responseData = response.body().string();
		ResponseBodyDTO resp = JsonUtils.toObject(responseData, ResponseBodyDTO.class);
		ResponseDTO responseDto = new ResponseDTO();
		responseDto.setBody(resp);
		responseDto.setCode(response.networkResponse().code());
		responseDto.setHeaders(response.headers().toString());
		RequestDTO requestdto = new RequestDTO();
		requestdto.setBody(buffer.readUtf8());
		requestdto.setHeaders(request.headers().toString());
		responseDto.setRequest(requestdto);
		String respo = responseDto.toString();
		return respo;
	}
	
	@Override
	public TinResponse checkTin(String tin, String API_URL, String username, String password) throws Exception {
		ResponsePojo rsp = this.getConnected(API_URL, username, password);
		String token = rsp.getResult().getToken();
		String RESSOURCE = "checkTIN";
		String tok ="Bearer "+token;
		OkHttpClient client = new OkHttpClient().newBuilder().followRedirects(false).build();
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
	    builder.hostnameVerifier(new HostnameVerifier() {
	        @Override
	        public boolean verify(String hostname, SSLSession session) {
	            return true;
	        }
	    });
	    
	    TinRequest tinrequest = new TinRequest(tin);
	   
    	MediaType mediaType = MediaType.parse("application/json");
    	RequestBody body = RequestBody.create(mediaType, JsonUtils.toJson(tinrequest));
    	Request request = new Request.Builder()
    			.url(API_URL+RESSOURCE)
    			.method("POST", body)
			 	.addHeader("Authorization", tok)
			 	.addHeader("Content-Type", "application/json")
			 	.build();
    	Buffer buffer = new Buffer();
    	request.body().writeTo(buffer);
		Response response = client.newCall(request).execute();
		String responseData = response.body().string();
		TinResponse re = new TinResponse();
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> ................................... TIN response : "+responseData);
		TinFirstResponse resp = JsonUtils.toObject(responseData, TinFirstResponse.class);
		if(resp.isSuccess()) {
			re = JsonUtils.toObject(responseData, TinResponse.class);
		}else {
			re.setMessage(resp.getMessage());
			re.setSuccess(resp.isSuccess());
			re.setResult(null);
		}
		//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> ................................... TIN response : "+resp);
		return re;
	}
	
	
}
