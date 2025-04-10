package com.luna.EasyInvoice.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.luna.EasyInvoice.config.EnvironmentConfig;
import com.luna.EasyInvoice.dto.ItemDTO;
import com.luna.EasyInvoice.entities.ArticleInvoice;
import com.luna.EasyInvoice.entities.ArticleOrder;
import com.luna.EasyInvoice.entities.ArticleReference;
import com.luna.EasyInvoice.entities.Client;
import com.luna.EasyInvoice.entities.Invoice;
import com.luna.EasyInvoice.entities.InvoiceNumbering;
import com.luna.EasyInvoice.entities.InvoiceValidationNumbering;
import com.luna.EasyInvoice.entities.Order;
import com.luna.EasyInvoice.entities.Organisation;
import com.luna.EasyInvoice.entities.QuotationNumbering;
import com.luna.EasyInvoice.entities.RubricItem;
import com.luna.EasyInvoice.pojo.ArticleInvoicePojo;
import com.luna.EasyInvoice.pojo.ArticleQPojo;
import com.luna.EasyInvoice.pojo.InvoicePojo;
import com.luna.EasyInvoice.pojo.OrderPojo;
import com.luna.EasyInvoice.pojo.OrganisationPojo;
import com.luna.EasyInvoice.service.implementation.ArticleReferenceServImpl;
import com.luna.EasyInvoice.service.implementation.ArticleServImpl;
import com.luna.EasyInvoice.service.implementation.ClientServImpl;
import com.luna.EasyInvoice.service.implementation.DebtRecoveryService;
import com.luna.EasyInvoice.service.implementation.InvoiceNumberingServiceImpl;
import com.luna.EasyInvoice.service.implementation.InvoiceServiceImpl;
import com.luna.EasyInvoice.service.implementation.InvoiceValidationNumberingServiceImpl;
import com.luna.EasyInvoice.service.implementation.OrderServiceImpl;
import com.luna.EasyInvoice.service.implementation.OrganisationServImpl;
import com.luna.EasyInvoice.service.implementation.QuotationNumberingServiceImpl;
import com.luna.EasyInvoice.service.implementation.TaxServImpl;
import com.luna.EasyInvoice.service.implementation.UserPrincipal;
import com.luna.EasyInvoice.utility.Response;
import com.luna.EasyInvoice.utility.UtilitiesService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@AllArgsConstructor
public class InvoiceController {

	private final ClientServImpl clientserviceimpl;
	private final OrderServiceImpl orderserviceimpl;
	private final DebtRecoveryService debtservice;
	private final QuotationNumberingServiceImpl quotationnumberingservice;
	private final InvoiceNumberingServiceImpl invoicenumberingservice;
	private final TaxServImpl taxserviceimpl;
	private final InvoiceServiceImpl invoiceserviceimpl;
	private final ArticleServImpl articleserviceimpl;
	private final ArticleReferenceServImpl articlereference;
	private final UtilitiesService utilityservice;
	private final InvoiceValidationNumberingServiceImpl invoiceValidationNumberingServiceImpl;
	private final Environment environment;
	private final OrganisationServImpl organisationserviceimpl;
	
//	@Autowired
//	EnvironmentConfig environmentConfig;
	
	@GetMapping("/invoices")
	public String index(Model model) {
		return this.index(1,model);
	}
	@GetMapping("/invoices/{pageno}")
	public String index(@PathVariable() int pageno, Model model) {
		int pagesize = Integer.valueOf(this.environment.getProperty("config.pagesize"));
		Page<Invoice> invoices = this.invoiceserviceimpl.fetch(pageno,pagesize); //int pageno, int pagesize
		if(this.utilityservice.checkIfHasRole("ROLE_ADMIN")) {
			model.addAttribute("organisation", this.organisationserviceimpl.findOrganisation().get(0));
		}else {
			model.addAttribute("organisation", this.utilityservice.getOrganisationFromConnectedUser());
		}
		model.addAttribute("clients", this.clientserviceimpl.findClient());
		model.addAttribute("jsName", "invoiceMan.js"); 
		model.addAttribute("invoices", invoices.getContent());
		model.addAttribute("currentPage", pageno);
		model.addAttribute("totalpages",invoices.getTotalPages());
		model.addAttribute("size", pagesize);
		return "invoice/invoices";
	}
	@PreAuthorize("hasAnyRole('COM_MANAGER','COM_HEAD_FINA','COM_CASH')")
	@GetMapping("/invoice")
	public String register(Model model) {
		model.addAttribute("organisation", this.utilityservice.getOrganisationFromConnectedUser());
		model.addAttribute("clients", this.clientserviceimpl.findClient());
		model.addAttribute("articles",this.articlereference.findArticle(true));
		model.addAttribute("taxs", this.taxserviceimpl.getActiveTaxList());
		//model.addAttribute("jsName", "super.js"); //super.js
		model.addAttribute("jsName", "invoiceMan.js"); 
		return "invoice/invoice";
	}
	
	@PostMapping("/searchOByC")
	public @ResponseBody Response search(@RequestParam String client, HttpServletRequest request) {
		Response response = new Response();
		Client cli = this.clientserviceimpl.findSingleClient(Long.valueOf(client.trim()));
		List<Order> orders = this.orderserviceimpl.fetch(cli);
		List<Invoice> invoices = new ArrayList<>();
		if(orders.size()>0) {
			List<OrderPojo> orderP = new ArrayList<>();
			for(Order ord : orders) {
				invoices = this.invoiceserviceimpl.fetch(ord);
				if(invoices.size() > 0) {
					double totalFact = 0.0;
					for(Invoice in : invoices) {
						totalFact = totalFact + in.getTotalAmount();
					}
					if(Double.compare(totalFact, ord.getTotalAmount()) < 0) {
						OrderPojo ordo= new OrderPojo();
						ordo.setRef(ord.getRef());
						ordo.setId(ord.getId());
						orderP.add(ordo);
					}
				}else if(invoices.size() <= 0) {
					OrderPojo ordo= new OrderPojo();
					ordo.setRef(ord.getRef());
					ordo.setId(ord.getId());
					orderP.add(ordo);
				}
			}
			if(orderP.size() > 0) {
				List<Object> obj = new ArrayList<>();
				obj.add(orderP);
				response.setBody(obj);
				response.setCode(1);
				response.setDescription("SUCCESS");
			}else {
				response.setCode(2);
				response.setDescription("Il n'y a pas de bon de commande qui n'a pas encore été utilisé pour ce client");
			}
		}
		return response;
	}
	
	@GetMapping("/editInvoice/{id}")
	public String editInvoice(@PathVariable() Long id, Model model) {
		
		Invoice invoice = this.invoiceserviceimpl.fetch(id);
		if(invoice.getVatAmount()>0){
			model.addAttribute("taxStatus", "1");
		}else {
			model.addAttribute("taxStatus", "0");
		}
		model.addAttribute("jsName", "editInvoice.js");
		model.addAttribute("invoice", invoice); 
		System.out.println(invoice.getReferenceNumber());		
		return "invoice/editInvoice";
	}
	
	@PostMapping("/orderManagement")
	public @ResponseBody Response orderManagement(@RequestParam String id, HttpServletRequest request) {
		request.getSession().setAttribute("ART_INV_SES", null);
		Response response = new Response();
		Order order = this.orderserviceimpl.fetch(Long.valueOf(id));
		List<ArticleInvoicePojo> listArt = new ArrayList<>();
		List<ArticleOrder> arts = this.invoiceserviceimpl.getArticleStatus2(order);
		System.out.println("Size : "+ +arts.size());
		for(ArticleOrder ar : arts) {
			//System.out.println("Voila");
			ArticleInvoicePojo art = new ArticleInvoicePojo();
			art.setDesignation(ar.getDescription());
			art.setId(ar.getId());
			art.setQuantity(ar.getQuantity());
			art.setTitle(ar.getTitle());
			art.setTotal(ar.getTotal());
			art.setUnity_price(ar.getUnityPrice());
			art.setTax(ar.getTotalVAT());
			art.setTaxRate(ar.getTaxRate());
			art.setTotalNVat(ar.getTotalNVAT());
			listArt.add(art);
		}
		request.getSession().setAttribute("ART_INV_SES", listArt);
		request.getSession().setAttribute("ORDER_SESSION", order);
		response.setCode(1);
		response.setDescription("SUCCESS");
		List<Object> lst = new ArrayList<>();
		lst.add(this.getOrderPojo(order));
		lst.add(listArt);
		response.setBody(lst);
		System.out.println(response);
		return response;
	}
	
	@PostMapping(value={"/dirArtInvoice","editInvoice/dirArtInvoice"})
	@Transactional
	public @ResponseBody Response dirArticleInvoice(@RequestParam String id) {
		Response response = new Response();
		ArticleInvoice article = this.articleserviceimpl.fetch(Long.valueOf(id));
		Invoice invoice = article.getInvoice();
		
		double totalvat = invoice.getVatAmount();
		double total = invoice.getTotalAmount();
		double subtotal = invoice.getSubtotal();
		
		invoice.setVatAmount(totalvat-article.getTax());
		invoice.setTotalAmount(total-article.getTotal());
		invoice.setSubtotal(subtotal-article.getTotalNVat());
		
		this.articleserviceimpl.delete(article);
		this.invoiceserviceimpl.save(invoice);
		
		response.setCode(1);
		response.setDescription("Success");
		return response;
	}
	
	@PostMapping(value={"/editArticleInvoice","editInvoice/editArticleInvoice"})
	public @ResponseBody Response editArticleInvoice(@RequestParam String id) {
		Response response = new Response();
		ArticleInvoice items = this.articleserviceimpl.fetch(Long.valueOf(id));
		Invoice invoice = items.getInvoice();
		List<ItemDTO> lst = new ArrayList<>();
		if(String.valueOf(invoice.getType()).equals("2")) {
			ItemDTO item = new ItemDTO();
			item.setArticleName(items.getTitle());
			item.setRemquantity(0);
			item.setRemrate(0);
			item.setRubricName("");
			item.setUnityprice(items.getUnityPrice());
			item.setTotquantity(items.getQuantity());
			lst.add(item);
			response.setBody(new ArrayList<>(lst));
		}else if(String.valueOf(invoice.getType()).equals("5")) {
			RubricItem article  = items.getRubricitem();
			
			//RubricItem article = this.rubricitemservice.fetch(Long.valueOf(articleid));
			double takQty = 0.0;
			if(article.getArticles().size()>0) {
				for(ArticleInvoice a : article.getArticles()) {
					takQty = takQty + a.getQuantity();
				}
			}
			ItemDTO item = new ItemDTO();
			item.setTotquantity(article.getQuantity());
			item.setTakquantity(takQty);
//			System.out.println(takQty);
//			System.out.println(item.getTotquantity());
			item.setRemquantity(item.getTotquantity()-takQty);
			item.setUnity(article.getUnity());
			item.setUnityprice(article.getUnity_price());
			item.setTaxRate(article.getTax());
			item.setArticleName(article.getArticle());
			item.setTotalTax(article.getTax_amount());
			item.setRubricName(article.getRubliquename());
			item.setArticleName(article.getArticle());
			double takrate = item.getTakquantity()*100/item.getTotquantity();
			double remrate = 100 - takrate;
			item.setRemrate(remrate);
			item.setTakrate(takrate);
			
			lst.add(item);
			response.setBody(new ArrayList<>(lst));
		}
		response.setCode(1);
		return response;
	}
	
	@PostMapping(value= {"/updateArticleInvoice","/editInvoice/updateArticleInvoice"})
	@Transactional
	public @ResponseBody Response updateArticleInvoice(@RequestParam String articleid, @RequestParam String newQty, @RequestParam String unityPrice, @RequestParam String articletitle, @RequestParam String typeinvoice) {
		Response response = new Response();
		if(typeinvoice.equals("5")) {
			ArticleInvoice items = this.articleserviceimpl.fetch(Long.valueOf(articleid));
			double unityPric = items.getUnityPrice();
			double origineQty = items.getQuantity();
			double vatrate = items.getTaxRate();
			double vatamount = items.getTax(); 
			double origineTotal = items.getTotal();
			double originSubTotal = items.getTotalNVat();
			RubricItem rub = items.getRubricitem();
			double totalQty = rub.getQuantity();		
			//-----------------------------------
			double newQuantity = Double.valueOf(newQty);	
			
			double newProgressRate = newQuantity*100/totalQty;
			double newAmaountWVAT = newQuantity*unityPric;
			double newTaxeAmount = 0.0;
			if(vatamount>0) {
				newTaxeAmount = newAmaountWVAT*vatrate/100;
			}
			double newTotal = newAmaountWVAT+newTaxeAmount;
			//--------- Calcul des differences -------------
			double diffSubTotal = originSubTotal - newAmaountWVAT;
			double diffVatAmount = vatamount - newTaxeAmount;
			double diffTotal = origineTotal - newTotal;
			//---------- Origine total on invoice -----------
			
			Invoice invoice = items.getInvoice();
			double origSubtotal = invoice.getSubtotal();
			double origTotal = invoice.getTotalAmount();
			double origVatAmount = invoice.getVatAmount();
			
			invoice.setSubtotal(origSubtotal-diffSubTotal);
			invoice.setVatAmount(origVatAmount-diffVatAmount);
			invoice.setTotalAmount(origTotal-diffTotal);
			
			items.setProgressRate(newProgressRate);
			items.setQuantity(newQuantity);	
			items.setTax(newTaxeAmount)	;	
			items.setTotal(newTotal)	;
			items.setTotalNVat(newAmaountWVAT);
			
			this.articleserviceimpl.saveArticle(items);
			this.invoiceserviceimpl.save(invoice);	
		}else {
			ArticleInvoice article = this.articleserviceimpl.fetch(Long.valueOf(articleid));
			
			double vatamount = article.getTax(); 
			double origineTotal = article.getTotal();
			double originSubTotal = article.getTotalNVat();
			
			double pu = Double.valueOf(unityPrice);
			double qty = Double.valueOf(newQty);
			double taxRate = 0.0;
			if(article.getInvoice().getVatAmount()>0) {
				taxRate = article.getTaxRate();
			}else {
				taxRate = 0.0;
			}
			article.setDescription(typeinvoice);
			article.setQuantity(qty);
			article.setTotalNVat(qty*pu);
			article.setTax(article.getTotalNVat()*taxRate/100);
			article.setTitle(articletitle);
			article.setTotal(article.getTotalNVat()+article.getTax());
			article.setUnityPrice(pu);
			
			//-----------------------------------
			double newvatamount = article.getTax(); 
			double newTotal = article.getTotal();
			double newSubTotal = article.getTotalNVat();
			
			
			//--------- Calcul des differences -------------
			double diffSubTotal = originSubTotal - newSubTotal;
			double diffVatAmount = vatamount - newvatamount;
			double diffTotal = origineTotal - newTotal;
			
			//---------- Origine total on invoice -----------
			
			Invoice invoice = article.getInvoice();
			double origSubtotal = invoice.getSubtotal();
			double origTotal = invoice.getTotalAmount();
			double origVatAmount = invoice.getVatAmount();
			
			invoice.setSubtotal(origSubtotal-diffSubTotal);
			invoice.setVatAmount(origVatAmount-diffVatAmount);
			invoice.setTotalAmount(origTotal-diffTotal);
			
			//invoice.setCurrency(typeinvoice)
			this.invoiceserviceimpl.save(invoice);
			
			this.articleserviceimpl.saveArticle(article);
		}
		response.setCode(1);
		
		return response;
	}
	
	@PostMapping("/updateArticleSession")
	public @ResponseBody Response updateArticle(@RequestParam String id, HttpServletRequest request) {
		Response response = new Response();
		List<ArticleInvoicePojo> carts = (List<ArticleInvoicePojo>) request.getSession().getAttribute("ART_INV_SES");
		if(carts.size()>0) {
			int row = 0;
			List<Object> body = new ArrayList<>();
			for(ArticleInvoicePojo sal : carts) {
				if(sal.getId().compareTo(Long.valueOf(id))==0) {
					body.add(sal);
					break;
				}
				row++;
			}
			response.setCode(1);
			response.setDescription("Success");
			response.setBody(body);
		}
		return response;
	}
	
	@PostMapping("/addArticleToSession")
	public @ResponseBody Response addArticle(
			@RequestParam String qty, 
			@RequestParam String art, 
			@RequestParam String unityP, 
			@RequestParam String unity, 
			@RequestParam String taxRate, 
			@RequestParam String taxAmnt, 
			@RequestParam String totWT, 
			@RequestParam String total,
			@RequestParam String calc_vat,
			HttpServletRequest request
			) {
		Response resp = new Response();
		
		//List<Object>  cartse = (List<Object>) request.getSession().getAttribute("ART_INV_SES_DIR"); 
		ArticleReference ar = this.articlereference.findArticle(Long.valueOf(art));
		ArticleInvoicePojo articlep = new ArticleInvoicePojo(); 
		
		int coefficient = 1;
		if(calc_vat.equalsIgnoreCase("2")) {
			coefficient=1;
		}else {
			coefficient=0;
		}
		
		double tax = coefficient * Double.valueOf(totWT) * Double.valueOf(taxRate)/100;
		double totalFi  = Double.valueOf(totWT) + tax; 
		
		articlep.setDesignation(ar.getTitle());
		articlep.setTitle(ar.getDescription());
		articlep.setId(Long.valueOf(art));
		articlep.setUnity(unity);
		//articlep.setDesignation(total
		articlep.setQuantity(Double.valueOf(qty));
		//articlep.setTax(Double.valueOf(taxAmnt));
		articlep.setTax(tax);
		articlep.setTaxRate(Double.valueOf(taxRate));
		//articlep.setTotal(Double.valueOf(total));
		articlep.setTotal(totalFi);
		articlep.setTotalNVat(Double.valueOf(totWT));
		articlep.setUnity_price(Double.valueOf(unityP));
		if(request.getSession().getAttribute("ART_INV_SES_DIR")==null) {
			
			List<ArticleInvoicePojo> arts = new ArrayList<>();
			arts.add(articlep);
			request.getSession().setAttribute("ART_INV_SES_DIR", arts);
			resp.setCode(1);
			resp.setDescription("Success");
			List<Object> obj = new ArrayList<>();
			obj.add(articlep);
			resp.setBody(obj);
			
		}else {
			//check the article
			List<ArticleInvoicePojo> cartse = (List<ArticleInvoicePojo>) request.getSession().getAttribute("ART_INV_SES_DIR");
			boolean isPresent = false;
			for(ArticleInvoicePojo a : cartse) {
				if(a.getId().toString().equals(articlep.getId().toString())) {
					isPresent=true;
				}
			}
			if(isPresent) {
				List<Object> obj = new ArrayList<>();
				for(ArticleInvoicePojo arti : cartse) {
					obj.add(arti);
				}
				resp.setBody(obj);
				resp.setCode(2);
				resp.setDescription("ARTICLE DEJA EXISTANT. PRIERE DE BIEN VOULOIR SOIT LE SUPPRIMER D'ABORD AVANT DE L'AJOUTER.");
				
			}else {
				List<ArticleInvoicePojo> arts = (List<ArticleInvoicePojo>) cartse;
				arts.add(articlep);
				request.getSession().setAttribute("ART_INV_SES_DIR", arts);
				cartse = (List<ArticleInvoicePojo>) request.getSession().getAttribute("ART_INV_SES_DIR");
				resp.setCode(1);
				List<Object> obj = new ArrayList<>();
				for(ArticleInvoicePojo arti : cartse) {
					obj.add(arti);
				}
				resp.setDescription("SUCCESS");
				resp.setBody(obj);
			}
			//
		}
		return resp;
	}
	
	@PostMapping("/deleteInvoiceArticle")
	public @ResponseBody Response deleteElementOnList(
			@RequestParam String id,
			HttpServletRequest request
			) {
		Response response = new Response();	
		List<ArticleInvoicePojo> carts = (List<ArticleInvoicePojo>) request.getSession().getAttribute("ART_INV_SES_DIR");
		System.out.println("Size : "+carts.size());
		if(carts.size()>0) {
			int row = 0;
			for(ArticleInvoicePojo sal : carts) {
				if(sal.getId().compareTo(Long.valueOf(id))==0) {
					carts.remove(row);
					break;
				}
				row++;
			}
			request.getSession().setAttribute("ART_INV_SES_DIR", carts);
			response.setCode(1);
			response.setDescription("Success");
			List<Object> body = new ArrayList<>();
			for(ArticleInvoicePojo ge : carts) {
				body.add(ge);
			}
			response.setBody(body);
		}
		return response;
	}
	
	@PostMapping("/updatecartsElement")
	public @ResponseBody Response updateElement(
			@RequestParam String id,
			@RequestParam String qte,
			@RequestParam String total, 
			@RequestParam String tax,
			@RequestParam String order_id,
			@RequestParam String tax_confirm, 
			@RequestParam String tax_amnt_confirm,
			HttpServletRequest request) {
		System.out.println("Vyemere nivyo .......");
		Response response = new Response();
		Order ord = this.orderserviceimpl.fetch(Long.valueOf(order_id));
		List<ArticleInvoicePojo> carts = (List<ArticleInvoicePojo>) request.getSession().getAttribute("ART_INV_SES");
		List<Object> body = new ArrayList<>();
		//return response;
		HashMap<Boolean, List<ArticleInvoicePojo>> result = this.checkArticle(ord, Long.valueOf(id) , Double.valueOf(qte), Double.valueOf(total), Double.valueOf(tax), request);
		if(result.get(true)!=null) {
			request.getSession().setAttribute("ART_INV_SES", null);
			for(ArticleInvoicePojo a : result.get(true)) {
				body.add(a);
			}
			request.getSession().setAttribute("ART_INV_SES", result.get(true));
			response.setCode(1);
			response.setDescription("Success");
			response.setBody(body);
		}else {
			response.setCode(2);
			body=null;
			response.setDescription("Vous essayez de surpasser les quantités disponibles sur votre bon de commande");
			request.getSession().setAttribute("ART_INV_SES", result.get(false));
			
		}
		return response;
	}
	
	@PostMapping("/direSaveInvoice")
	@Transactional
	public @ResponseBody Response directSave(
				@RequestParam String isEntreprise, 
				@RequestParam String cli_name, 
				@RequestParam String cli_telephone, 
				@RequestParam String cli_nif, 
				@RequestParam String cli_contact, 
				@RequestParam String cli_addr, 
				@RequestParam String cli_prov, 
				@RequestParam String cli_commune, 
				@RequestParam String cli_district,
				@RequestParam String quo_taxe, 
				@RequestParam String is_vat_subj,
				@RequestParam String quo_currency, 
				@RequestParam String inv_pyt_mod,
				@RequestParam String inv_tax,
				@RequestParam String inv_sub_tot, 
				@RequestParam String inv_total, 
				@RequestParam String quo_comment,
				HttpServletRequest request
			) {
		
		//System.out.println("Commentaire : "+quo_comment);	
		
		
		Response resp = new Response();
		String vat = inv_tax.replace(" ", "");
		String subTotale = inv_sub_tot.replace(" ", "");
		String totale = inv_total.replace(" ", "");
		if(request.getSession().getAttribute("ART_INV_SES_DIR")==null) {
			resp.setCode(0);
			resp.setDescription("Pas d'article sur la facture");
		}else {
			List<ArticleInvoicePojo> cartse = (List<ArticleInvoicePojo>) request.getSession().getAttribute("ART_INV_SES_DIR");
			if(cartse.size()<=0) {
				resp.setCode(0);
				resp.setDescription("Pas d'article sur la facture");
			}else {
				String regBy = this.utilityservice.getConnectedUserInfo().getFirstName()+" "+this.utilityservice.getConnectedUserInfo().getLastName();
				Organisation organisation = this.utilityservice.getOrganisationFromConnectedUser();
				Client client = this.clientserviceimpl.findClientByName(cli_name);//.findClient(cli_telephone);				
				Invoice invoice = new Invoice();
				invoice.setType(2);
				invoice.setCreatedBy(regBy);
				invoice.setCreatedOn(new Date());
				invoice.setCurrency(quo_currency);
				invoice.setDelivaryAddress(cli_prov+","+cli_commune+","+cli_district);
				invoice.setInvoiceDate(new Date());
				invoice.setOrder(null);
				invoice.setOrganisation(organisation);
				invoice.setPaymentMode(inv_pyt_mod);
				invoice.setStatus(1);
				invoice.setPrintFormat("1");
				invoice.setClient(client);
				invoice.setOrganisation(organisation);
				invoice.setReferenceNumber(this.getInvoiceReference(this.utilityservice.getOrganisationFromConnectedUser().getId()));
				invoice.setTotalAmount(Math.round(Double.valueOf(totale)) );
				invoice.setVatAmount(Math.round(Double.valueOf(vat)));
				invoice.setSubtotal(Math.round(Double.valueOf(subTotale)));
				invoice.setComment(quo_comment.trim());
				
				invoice = this.invoiceserviceimpl.save(invoice);
				
				//item
				//Article
				for(ArticleInvoicePojo art : cartse) {
					ArticleInvoice artInv = new ArticleInvoice();
					artInv.setDescription(art.getDesignation());
					artInv.setInvoice(invoice);
					artInv.setQuantity(art.getQuantity());
					artInv.setRefOnOrder(art.getId());
					artInv.setTax(art.getTax());
					artInv.setTaxRate(art.getTaxRate());
					artInv.setTotalNVat(art.getTotalNVat());
					artInv.setTitle(art.getTitle());
					artInv.setTotal(art.getTotal());
					artInv.setUnityPrice(art.getUnity_price());
					artInv.setUnity(art.getUnity());					
					artInv.setArticleOrder(null);		
					this.articleserviceimpl.saveArticle(artInv);
				}
			}
		}
		return resp;
	}
	
	@PostMapping("/deleteInvoiceElement")
	public @ResponseBody Response deleteElement(@RequestParam String id, HttpServletRequest request) {
		Response response = new Response();
		System.out.println("Id : "+id);
		List<ArticleInvoicePojo> carts = (List<ArticleInvoicePojo>) request.getSession().getAttribute("ART_INV_SES");
		System.out.println("Size : "+carts.size());
		if(carts.size()>0) {
			int row = 0;
			for(ArticleInvoicePojo sal : carts) {
				if(sal.getId().compareTo(Long.valueOf(id))==0) {
					carts.remove(row);
					break;
				}
				row++;
			}
			request.getSession().setAttribute("ART_INV_SES", carts);
			response.setCode(1);
			response.setDescription("Success");
			List<Object> body = new ArrayList<>();
			for(ArticleInvoicePojo ge : carts) {
				body.add(ge);
			}
			response.setBody(body);
		}
		return response;
	}
	
	@Transactional
	@PostMapping("/saveInvoice")
	public @ResponseBody Response save(
			@RequestParam String id,
			@RequestParam String vat,
			@RequestParam String subTotal,
			@RequestParam String pyt_mode,
			@RequestParam String total,
			String comment,
			HttpServletRequest request) {
		
		//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Ubwo burya ni ngaha .........");
		String vate = (vat.replace("BIF", "")).replace(" ", "");
		
		String subTotale = (subTotal.replace("BIF", "")).replace(" ", "");
		
		String totale = (total.replace("BIF", "")).replace(" ", "");
		
		Response response = new Response();
		//recuperation des articles sur la facture
		List<ArticleInvoicePojo> articles = (List<ArticleInvoicePojo>) request.getSession().getAttribute("ART_INV_SES");
		
		Order order = this.orderserviceimpl.fetch(Long.valueOf(id));
		//Invoice d'abord
		UserPrincipal princ = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();  
		String createdBy = princ.getUserName();
		//String org = princ.getOrganisation();
		Invoice invoice = new Invoice();
		
		invoice.setCreatedBy(createdBy);
		invoice.setCreatedOn(new Date());
		invoice.setOrganisation(this.utilityservice.getOrganisationFromConnectedUser());
		invoice.setComment(comment);
		invoice.setDelivaryAddress(order.getDelivaryAddress());
		invoice.setInvoiceDate(new Date());
		invoice.setPaymentMode(pyt_mode);
		invoice.setOrder(order);
		//invoice.setPaymentMode("");
		invoice.setStatus(1);
		//invoice.setCurrency(createdBy)
		//referencement de la facture
		invoice.setReferenceNumber(this.getInvoiceReference(this.utilityservice.getOrganisationFromConnectedUser().getId()));
		invoice.setTotalAmount(Double.valueOf(totale));
		invoice.setVatAmount(Double.valueOf(vate));
		invoice.setPrintFormat("1");
		invoice.setSubtotal(Double.valueOf(subTotale));
		invoice = this.invoiceserviceimpl.save(invoice);
		//List<ArticleInvoice> arts = new ArrayList<>();
		
		for(ArticleInvoicePojo ar : articles) {
			ArticleInvoice article = new ArticleInvoice();
			article.setDescription(ar.getDesignation());
			article.setInvoice(invoice);
			article.setQuantity(ar.getQuantity());
			article.setRefOnOrder(ar.getId());
			article.setTax(ar.getTax());
			article.setTaxRate(ar.getTaxRate());
			article.setTotalNVat(ar.getTotalNVat());
			article.setTitle(ar.getTitle());
			article.setTotal(ar.getTotal());
			article.setUnityPrice(ar.getUnity_price());
			article.setArticleOrder((ArticleOrder)this.articleserviceimpl.findArticlebyid(ar.getId()));			
			this.articleserviceimpl.saveArticle(article);
			//arts.add(article);
		}
		response.setCode(1);
		response.setDescription("SUCCESS");
		return response;
	}
	
	@PostMapping(value = {"/searchInvoice","/invoices/searchInvoice", ""})
	public @ResponseBody Response search(
				@RequestParam String id
			){
		Response response = new Response();
		//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>> ID : "+id);
		Invoice invoice = this.invoiceserviceimpl.fetch(Long.valueOf(id));
		
		//System.out.println(invoice.toString());
		if(invoice!=null) {
			Organisation or = new Organisation();
			if(this.utilityservice.checkIfHasRole("ROLE_ADMIN")) {
				or = invoice.getOrganisation();
			}else {
				or = this.utilityservice.getOrganisationFromConnectedUser();
			}
			OrganisationPojo org = new OrganisationPojo();
			org.setAccountTitle(or.getAccountTitle());
			org.setActivitySecter(or.getActivitySecter());
			org.setAddressAvenue(or.getAddressAvenue());
			org.setAddressCommune(or.getAddressCommune());
			org.setAddressNumber(or.getAddressNumber());
			org.setAddressProvince(or.getAddressProvince());
			org.setAddressQuartier(or.getAddressQuartier());
			org.setBankAccount(or.getBankAccount());
			org.setBankName(or.getBankName());
			org.setCTPayer(or.isCTPayer());
			org.setFiscalCenter(or.getFiscalCenter());
			org.setJuridictionForm(or.getJuridictionForm());
			org.setLogo(or.getLogo());			
			org.setLVPayer(or.isLVPayer());
			org.setName(or.getName());
			org.setPostalNumber(or.getPostalNumber());
			org.setRepresentativeName(or.getRepresentativeName());
			org.setTelephone(or.getTelephone());
			org.setTin(or.getTin());
			org.setTradeNumber(or.getTradeNumber());
			org.setVATPayer(org.isVATPayer());
			org.setId(or.getId());			
			
			InvoicePojo inv = new InvoicePojo();
			inv.setDelivaryAddress(invoice.getDelivaryAddress());
			inv.setId(invoice.getId());
			//inv.setOrder(invoice.getOrder());
			inv.setPrintFormat("1");
			inv.setStatus(invoice.getStatus());
			inv.setInvoiceDate(invoice.getInvoiceDate());
			inv.setPaymentMode(invoice.getPaymentMode());
			inv.setReferenceNumber(invoice.getReferenceNumber());
			inv.setSubtotal(invoice.getSubtotal());			
			inv.setTotalAmount(invoice.getTotalAmount());
			inv.setVatAmount(invoice.getVatAmount());
			inv.setCurrency("BIF");
			inv.setOrganisation(org);
			List<ArticleInvoicePojo> arts = new ArrayList<>();
			for(ArticleInvoice art : invoice.getArticles()) {
				ArticleInvoicePojo ar = new ArticleInvoicePojo();
				ar.setDesignation(art.getDescription());
				ar.setId(art.getId());
				ar.setQuantity(art.getQuantity());
				ar.setTitle(art.getTitle());
				ar.setTotal(art.getTotal());
				ar.setUnity_price(art.getUnityPrice());
				ar.setTax(art.getTax());
				ar.setTaxRate(art.getTaxRate());
				ar.setTotalNVat(art.getTotalNVat());
				ar.setUnity(art.getUnity());
				arts.add(ar);
			}
			inv.setArticleInvoicePojo(arts);
			inv.setCliAdress(invoice.getClient().getAddInfos() + "\n"+invoice.getClient().getEmail()+"\n"+invoice.getClient().getTelephone());
			inv.setCliName(invoice.getClient().getName());
			inv.setCliTin(invoice.getClient().getTIN());
			inv.setCliIsVAT(invoice.getClient().isVatSubject());
			inv.setInvoiceDate(invoice.getInvoiceDate());
			inv.setRegistrationperson(invoice.getCreatedBy());
			inv.setInvoiceRef(invoice.getReferenceNumber());
			
			inv.setValidatedDate(invoice.getValidatedDate());
			inv.setValidationdateDate(invoice.getValidatedDate());
			inv.setValidationref(invoice.getValidationref());
			inv.setValidationPerson(invoice.getValidatedPerson());
			
			
			inv.setCancallationDate(invoice.getCancelledDate());
			inv.setCancellationPerson(invoice.getCancelledPerson());
			inv.setCancellationreason(invoice.getCancelRaison());	
			
			
			
			//inv.setEbmsACK(invoice.getSignature());
			inv.setIdEBMSSignature(invoice.getSignature());
			
			String ebmsack = "";
			if(invoice.getEbmsACK()!=null) {
				if(!invoice.getEbmsACK().isEmpty()) {
					if(invoice.getEbmsACK().length()>90) {
						ebmsack = invoice.getEbmsACK().substring(0, 90)+"[...]";
					}else ebmsack = invoice.getEbmsACK();
				}
			}
			
			inv.setEbmsACK(ebmsack);
			inv.setEbmsMsg(invoice.getEbmsMsg());
			
			
			response.setCode(1);
			response.setDescription("SUCCESS");
			List<Object> obj = new ArrayList<>();
			obj.add(inv);
			response.setBody(obj);
		}
		return response;
	}
	
	@PostMapping("/validateI")
	@Transactional
	public @ResponseBody Response validate(@RequestParam String id) {
		Response response = new Response(); 
		Invoice invoice = this.invoiceserviceimpl.fetch(Long.valueOf(id));
		
		Date validationDate = new Date();
		
		String signDatetemplate = this.utilityservice.dateToString(validationDate,"yyyyMMddHHmmss");
		
		String obrDateFormat = this.utilityservice.dateToString(validationDate,"yyyy-MM-dd HH:mm:ss");
		
		invoice.setValidatedDate(validationDate);
		
		invoice.setValidatedPerson(this.utilityservice.getConnectedUserInfo().getLastName()+" "+this.utilityservice.getConnectedUserInfo().getFirstName());		
		
		
		
		Organisation organ = invoice.getOrganisation();
		String signature = "";
		
		if(invoice!=null) {
			if(invoice.getStatus()==1) {
				invoice.setValidatedDate(validationDate);
				invoice.setValidationref(this.getInvoiceValidationNumber(organ));
				if(organ.getInterconnections().size()>0) {
					String username= organ.getInterconnections().get(0).getUsername();
					signature = organ.getTin()+"/"+username+"/"+signDatetemplate+"/"+invoice.getValidationref();
					//signature = organ.getTin()+"/"+username+"/"+this.utilityservice.dateToString(invoice.getInvoiceDate(),"yyyyMMddhhmmss")+"/"+invoice.getReferenceNumber();
				}
				//String 
				invoice.setSignature(signature);
				invoice.setSignatureDate(validationDate);
				invoice.setObrDate(obrDateFormat);
				invoice.setStatus(2);
				invoice= this.invoiceserviceimpl.save(invoice);
				this.debtservice.recordDebt(invoice);
				response.setCode(1);
				response.setDescription("SUCCESS");
			}else {
				response.setCode(-1);
				response.setDescription("IMPOSSIBLE TO UPDATE THE STATUS");
			}
		}
		return response;
	}
	
	private OrderPojo getOrderPojo(Order order){
		OrderPojo orderp = new OrderPojo();
		orderp.setClient(order.getClient());
		orderp.setComments(order.getComments());
		orderp.setDelivaryAddress(order.getDelivaryAddress());
		orderp.setGrandTotal(order.getTotalAmount());
		orderp.setId(order.getId());
		orderp.setInvoiceAddress(order.getInvoiceAddress());
		orderp.setOrderDate(order.getOrderDate());
		orderp.setRef(order.getRef());
		orderp.setSubTotal(order.getSubTotal());
		orderp.setTotalTax(order.getVatAmount());
		orderp.setIsTaxable(order.getQuotation().getTaxApplicable());
		List<ArticleQPojo> articles = new ArrayList<>();
		for(ArticleOrder art : order.getArticles()) {
			ArticleQPojo a = new ArticleQPojo();
			a.setId(art.getId());
			a.setDescription(art.getDescription());
			a.setTitle(art.getTitle());
			a.setQuantity(art.getQuantity());
			a.setUnityPrice(art.getUnityPrice());
			a.setTotalNVAT(art.getTotalNVAT());
			a.setTotalVat(art.getTotalVAT());
			a.setTotalWVAT(art.getTotal());
			a.setVat(art.getTaxRate());
			articles.add(a);
		}
		orderp.setArticles(articles);
		return orderp;
	}
	
	private HashMap<Boolean, List<ArticleInvoicePojo>> checkArticle(Order order, Long id, double quantity, double amount, double vat,HttpServletRequest request) {
		HashMap<Boolean, List<ArticleInvoicePojo>> result = new HashMap<>();
		List<ArticleInvoicePojo> articles_session = (List<ArticleInvoicePojo>) request.getSession().getAttribute("ART_INV_SES");
		List<ArticleInvoicePojo> listArt = new ArrayList<>();
		List<ArticleOrder> ll = this.invoiceserviceimpl.getArticleStatus(order);
		System.out.println(ll.size());
		for(ArticleOrder ar : ll) {
			ArticleInvoicePojo art = new ArticleInvoicePojo();
			art.setDesignation(ar.getDescription());
			art.setId(ar.getId());
			art.setQuantity(ar.getQuantity());
			art.setTitle(ar.getTitle());
			art.setTotal(ar.getTotal());
			art.setUnity_price(ar.getUnityPrice());
			art.setTax(ar.getTotalVAT());
			art.setTaxRate(ar.getTaxRate());
			art.setTotalNVat(ar.getTotalNVAT());
			listArt.add(art);
		}
		boolean res = false;
		ArticleInvoicePojo article = new ArticleInvoicePojo();
		for(ArticleInvoicePojo art : listArt) {
			if(art.getId().compareTo(id)==0) {
				double taxamt = 0.0;
				System.out.println("Session art quet : "+art.getQuantity());
				System.out.println("Rec quet : "+quantity);
				System.out.println("Session tax : "+art.getTax());
				if(art.getQuantity()>=quantity) {
					if(art.getTax()>0) {
						taxamt = quantity*art.getUnity_price()*Double.valueOf(vat)/100;
					}
					res=true;
				}else {
					taxamt=0.0;
					res=false;
				}
				article.setQuantity(quantity);
				article.setTax(taxamt);
				article.setTotalNVat(quantity*art.getUnity_price());
				article.setTotal(taxamt+(quantity*art.getUnity_price()));
				break;
			}
		}
		
		for(ArticleInvoicePojo art : articles_session) {
			if(art.getId().compareTo(id)==0) {
				art.setQuantity(article.getQuantity());
				art.setTax(article.getTax());
				art.setTotalNVat(article.getTotalNVat());
				art.setTotal(article.getTotal());
				break;
			}
		}		
		result.put(res, articles_session);
		return result;
	}
	
	private String getReference(Long org_id) {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		List<QuotationNumbering> num = this.quotationnumberingservice.find(year, org_id);
		QuotationNumbering quot = new QuotationNumbering();
		int ref = 1;
		if(num.size()>0) {
			ref= num.get(0).getNext();
		}
		quot.setCurrent(ref);
		quot.setNext(ref+1);
		quot.setYear(year);
		quot.setOrgId(org_id);
		this.quotationnumberingservice.saveQuotation(quot);
		return year+"/QUOT/"+org_id+"/"+ref;
	}
	
	
	@PostMapping("/findInv")
	public @ResponseBody Response findInvoice(@RequestParam("id") String id) {
		Response response = new Response();
		Client client = this.clientserviceimpl.findSingleClient(Long.valueOf(id));
		response.setDescription(client.getName());
		return response;
	}
	
	
	@PostMapping("/findInvoice")
	public @ResponseBody Response findInvoicee(@RequestParam("id") String id) {
		Response response = new Response();
		Invoice invoice = this.invoiceserviceimpl.fetch(Long.valueOf(id));
		List<String> invoiceparam = new ArrayList<>();
		invoiceparam.add(invoice.getReferenceNumber());
		invoiceparam.add(this.utilityservice.dateToString(invoice.getInvoiceDate(), "dd/MM/yyyy"));
		invoiceparam.add(invoice.getClient().getName());
		response.setCode(1);
		List<Object> obj = new ArrayList<>(invoiceparam);
		response.setBody(obj);
		response.setDescription(invoice.getClient().getName());
		return response;
	}
	
	@PostMapping("/taxCalcul2")
	public @ResponseBody Response calculTax(HttpServletRequest request, @RequestParam String typ) {
		Response response = new Response();
		List<ArticleInvoicePojo> carts = (List<ArticleInvoicePojo>) request.getSession().getAttribute("ART_INV_SES_DIR");
		System.out.println("Sessions ........ : "+carts);
		if(carts!=null) {
			if(carts.size()>0) {
				int coefficient = 1;
				if(typ.equalsIgnoreCase("2")) {
					coefficient=1;
				}else {
					coefficient=0;
				}
				for(ArticleInvoicePojo sal : carts) {
					double vat = coefficient*sal.getTotalNVat()*sal.getTaxRate()/100;
					sal.setTax(vat);
					sal.setTotal(sal.getTax()+sal.getTotalNVat());		
				}
				response.setCode(1);
				response.setDescription("Success");
				
				List<Object> bod = new ArrayList<>();
				for(ArticleInvoicePojo ge : carts) {
					bod.add(ge);
				}
				response.setBody(bod);
				
			}else {
				response.setCode(0);
				response.setDescription("No items found on this quotation");
			}
		}
		return response;
	}
	@PostMapping("/cancelInv")
	public @ResponseBody Response cancel(@RequestParam String id,
			@RequestParam String motif) throws Exception
	{
		System.out.println("Annulation");
		Response rsp = new Response();
		Invoice invoice = this.invoiceserviceimpl.fetch(Long.valueOf(id));
		if(invoice != null) {
			log.info("Check if the invoice has the status 3 ");
			log.info("About to cancel the invoice");
			invoice.setCancelRaison(motif);
			if(invoice.getStatus()==3) {
				invoice.setStatus(0);
				invoice = this.invoiceserviceimpl.save(invoice);
				//this..cancelInvoice(invoice, invoice.getOrganisation());
				rsp.setCode(2);
				rsp.setDescription("SUCCESS");
			}else {
				invoice.setStatus(5);
				invoice = this.invoiceserviceimpl.save(invoice);
				rsp.setCode(1);
				rsp.setDescription("SUCCESS");
			}
		}
		return rsp;
	}
	
	/***
	 * Methode pour la gestion des factures a partir du projet d'execution
	 */
	
	@GetMapping("/invoiceExec")
	public String invoiceExec(Model model) {
		List<Client> clients = this.clientserviceimpl.findClient();
		model.addAttribute("clients", clients);
		model.addAttribute("jsName", "invoiceExec.js"); //super.js
		return "invoice/invoiceEx";
	}
	
	
	@GetMapping("/getInvoiceClient")
	public @ResponseBody void gettt() {
		List<Invoice> lst = this.invoiceserviceimpl.fetch();
		for(Invoice inv:lst) {
			System.out.println("UPDATE invoice SET client_id='"+inv.getOrder().getQuotation().getClient().getId()+"'  WHERE id='"+inv.getId()+"';");
		}
	}
	private String getInvoiceReference(Long org_id) {
		//1. Voir si le numero exist
		int year = Calendar.getInstance().get(Calendar.YEAR);
		String pre = "0000";
		List<InvoiceNumbering> num = this.invoicenumberingservice.find(year, this.utilityservice.getOrganisationFromConnectedUser().getId());
		InvoiceNumbering inv = new InvoiceNumbering();
		int ref = 1;
		if(num.size()>0) {
			ref= num.get(0).getNext();
		}
		inv.setCurrent(ref);
		inv.setNext(ref+1);
		inv.setYear(year);
		inv.setOrgId(org_id);
		this.invoicenumberingservice.saveInvo(inv);
		return ref+"/"+year;
	}
	private String getInvoiceValidationNumber(Organisation org) {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		String pre = "0000";
		List<InvoiceValidationNumbering> num = this.invoiceValidationNumberingServiceImpl.find(year, this.utilityservice.getOrganisationFromConnectedUser().getId());
		InvoiceValidationNumbering inv = new InvoiceValidationNumbering();
		int ref = 1;
		if(num.size()>0) {
			ref= num.get(0).getNext();
		}
		inv.setCurrent(ref);
		inv.setNext(ref+1);
		inv.setYear(year);
		inv.setOrgId(org.getId());
		this.invoiceValidationNumberingServiceImpl.saveInvo(inv);
		String de = "";
		if(String.valueOf(ref).length()<4) {
			de = pre.substring(String.valueOf(ref).length())+String.valueOf(ref)+"/"+year;
		}else {
			de=String.valueOf(ref)+"/"+year;
		}
		return de;
	}
	@PostMapping(value = {"/search","/invoices/search"})
	public String search(Model model, String status, String clientid, String validationref, String regNumber) {
		List<Invoice> invoices = new ArrayList<>();
		List<Integer> stas = new ArrayList<>();
		if(status.isEmpty()) {
			stas.add(0);
			stas.add(1);
			stas.add(2);
			stas.add(3);
			stas.add(4);
			stas.add(5);
			stas.add(-3);
		}else {
			if(status.equals("1")) {
				stas.add(1);
			}
			else if(status.equals("2")) {
				stas.add(2);
				stas.add(3);
			}else if(status.equals("3")) {
				stas.add(0);
				stas.add(5);
			}else if(status.equals("-3")) {
				stas.add(Integer.valueOf(status));
			}
		}
		if(!clientid.isEmpty()) {
			Client client = this.clientserviceimpl.findSingleClient(Long.valueOf(clientid));
			invoices = this.invoiceserviceimpl.getSearchedInvoice(client, stas, "%"+validationref+"%", "%"+regNumber+"%", this.utilityservice.getOrganisationFromConnectedUser());
		}else {
			System.out.println("List des status : "+stas);
			invoices = this.invoiceserviceimpl.getSearchedInvoice(stas, "%"+validationref+"%", "%"+regNumber+"%", this.utilityservice.getOrganisationFromConnectedUser());
		}
		
		model.addAttribute("invoices", invoices);
		model.addAttribute("clients", this.clientserviceimpl.findClient()); 
		model.addAttribute("clients", this.clientserviceimpl.findClient());
		model.addAttribute("validationref", validationref);
		model.addAttribute("regNumber", regNumber);
		model.addAttribute("clientid", clientid);
		model.addAttribute("status", status);
		
		return "invoice/invoices";
	}
	
	@GetMapping(value = {"/search","/invoices/search"})
	public String search(Model model) {
		return "redirect:/invoices/1";
	}
}
