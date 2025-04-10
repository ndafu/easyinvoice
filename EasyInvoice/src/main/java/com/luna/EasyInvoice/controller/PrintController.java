package com.luna.EasyInvoice.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.luna.EasyInvoice.dto.PrintArticleDTO;
import com.luna.EasyInvoice.entities.ArticleInvoice;
import com.luna.EasyInvoice.entities.Client;
import com.luna.EasyInvoice.entities.Invoice;
import com.luna.EasyInvoice.pojo.PrintPojo;
import com.luna.EasyInvoice.service.implementation.InvoiceServiceImpl;
import com.luna.EasyInvoice.utility.FrenchNumberToWords;
import com.luna.EasyInvoice.utility.UtilitiesService;

import ch.qos.logback.core.joran.conditional.ElseAction;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Controller
@Slf4j
public class PrintController {
	@Autowired
	private ResourceLoader resourceLoader;
	@Autowired
	InvoiceServiceImpl invoiceserviceimpl;
	
	@Autowired
	FrenchNumberToWords frenchNumberToWords;
	
	@Autowired
	UtilitiesService utilityservice;

	@PreAuthorize("hasRole('ROLE_COM_MANAGER')")
	@GetMapping(value = {"/invoices/printI", "/printI"})
	public ResponseEntity<byte[]> printInvoice(@RequestParam int iRef, @RequestParam() int avanc) throws MalformedURLException, IOException, JRException{
		
		Invoice invoice = this.invoiceserviceimpl.fetch(Long.valueOf(iRef));
		if(invoice.getPrintFormat().equals("1")) {
			return this.printInvoice(invoice);
		}else{
			return this.print(invoice, avanc);
		}
	}
	
	
	public ResponseEntity<byte[]> printInvoice(Invoice invoice) throws MalformedURLException, IOException, JRException {
		String orgName = invoice.getOrganisation().getName();
    	List<PrintPojo> articles = new ArrayList<>();
    	log.info("Status de la facture : "+invoice.getStatus());
    	for(ArticleInvoice i: invoice.getArticles()) {
    		PrintPojo p = new PrintPojo();
    		p.setTitle(i.getTitle());
    		p.setPrice(String.format(Locale.FRANCE, "%,.2f", i.getUnityPrice()));
    		p.setQuantity(String.format(Locale.FRANCE, "%,.2f", i.getQuantity()));
    		p.setTotal(String.format(Locale.FRANCE, "%,.2f", i.getTotalNVat()));
    		String unity = i.getUnity()==null?"":i.getUnity();
    		p.setUnity(unity);
    		p.setUnity_price(String.format(Locale.FRANCE, "%,.2f", i.getUnityPrice()));
    		p.setTotalNVAT(String.format(Locale.FRANCE, "%,.2f", i.getTotalNVat()));
    		articles.add(p);
    	}
    	JRBeanCollectionDataSource data = new JRBeanCollectionDataSource(articles);
    	String pathe = "";
    	pathe = resourceLoader.getResource("classpath:static/dir/invoice.jrxml").getFile().getAbsolutePath();
    	JasperReport compiledreport = JasperCompileManager.compileReport(pathe);
		HashMap<String,Object> param = new HashMap<>();
		this.getClass().getClassLoader();
		Client client = invoice.getClient();
		String lange = LocaleContextHolder.getLocale().toString();
		if(lange.equalsIgnoreCase("en")) {
			param.put("P_TIN", "TIN :");
			param.put("P_ISVATP", "VAT paye :");
			param.put("P_RC", "RC :");
			param.put("P_CENTRE_F", "Tax center : ");
			param.put("P_SECTOR_A", "Activity sector : ");
			param.put("P_INVOICE", "INVOICE");
			param.put("P_IS_TC_P", "Subject to Consumption Tax :");
			param.put("P_IS_PF_P", "Subject to Flat rate levy :");
			param.put("P_YES", "YES");
			param.put("P_NO", "NO");
			param.put("P_BANK_LBL","Bank account : ");
			param.put("P_PERS_PHYS_P", "Physical person");
			param.put("P_SOCIETE_P", "Company");
			//param.put("P_COMMENT", "• All required tours of fumigation as per LPO have been accomplished. ");
			param.put("P_COMMENT", "");
			param.put("P_THANKS", "Thank you for your business. It’s was a pleasure to work with you.");
			param.put("P_SINCER", "Sincerely yours,");
			param.put("P_TERMS", "Terms : Payment does not exceed 7 Days after service. ");
			param.put("P_LBL_NATURE","Service");
			param.put("P_LBL_REFERENCE","Reference");
			param.put("P_LBL_QTY", "Qty");
			param.put("P_LBL_UNIT_PRICE", "Unit Price");
			param.put("P_LBL_PVHTVA", "Subtotal");
			param.put("P_LBL_RISTOURNE", "Discount");
			param.put("P_LBL_VAT", "Tax");
			param.put("P_LBL_TVAC", "Total");
			param.put("P_PS", "Comments");
			param.put("P_PYT_KIND", "Kind of payment");
			param.put("P_PYT_CASH", "Cash");
			param.put("P_PYT_BANK", "Bank");
		}else {
			//label
			param.put("P_TIN", "NIF :");
			param.put("P_ISVATP", "Assujetti à la TVA :");
			param.put("P_RC", "RC :");
			param.putIfAbsent("P_CENTRE_F", "Centre fiscal : ");
			param.put("P_SECTOR_A", "Secteur d'activités : ");
			param.put("P_INVOICE", "FACTURE");
			param.put("P_IS_TC_P", "Assujetti à la TC :");
			param.put("P_IS_PF_P", "Assujetti à la PF :");
			param.put("P_YES", "OUI");
			param.put("P_NO", "NON");
			param.put("P_BANK_LBL","Compte bancaire : ");
			param.put("P_PERS_PHYS_P", "Personne physique ");
			param.put("P_SOCIETE_P", "Société");	
			param.put("P_COMMENT", "");
			//param.put("P_COMMENT", "• Tous les tours de fumigations, convenus dans le LPO ont été effectués. ");
			param.put("P_THANKS", "Nous vous remercions de nous avoir fait confiance. C'était un réel plaisir de travailler avec vous.");
			param.put("P_SINCER", "Cordialement,"); 
			param.put("P_TERMS", "Conditions : Le paiement doit être fait au maximum dans les 7 jours après L'éxecution du service.  ");
			param.put("P_LBL_NATURE","Nature du service");
			param.put("P_LBL_REFERENCE","Référence");
			param.put("P_LBL_QTY", "Qté");
			param.put("P_LBL_UNIT_PRICE", "PU");
			param.put("P_LBL_PVHTVA", "PVHTVA");
			param.put("P_LBL_RISTOURNE", "Ristourne");
			param.put("P_LBL_VAT", "TVA");
			param.put("P_LBL_TVAC", "TVAC");
			param.put("P_PS", "Commentaires");
			param.put("P_PYT_KIND", "Type de paiement");
			param.put("P_PYT_CASH", "Espèces");
			param.put("P_PYT_BANK", "Banques");
		}
		if(client.getTypeClient().equalsIgnoreCase("entr")) {
			param.put("P_CLI_NOTSOC", "");
			param.put("P_CLI_ISSOC", "X");
		}else {
			param.put("P_CLI_NOTSOC", "X");
			param.put("P_CLI_ISSOC", "");
		}
		
		
		param.put("P_PHYSIQUE", "X");
		
		
		param.put("P_ADDR_AV_NUM", invoice.getOrganisation().getAddressAvenue()+" n."+invoice.getOrganisation().getAddressNumber());
		param.put("P_ADDR_COM_QUARTIER", invoice.getOrganisation().getAddressCommune()+", "+invoice.getOrganisation().getAddressQuartier());
		
		//param.put("P_CLI_ADDRESS", invoice.getDelivaryAddress());
		String cliaddress = invoice.getClient().getProvince()+", "+invoice.getClient().getCommune()+", "+ invoice.getClient().getQuartier();
		
		param.put("P_CLI_ADDRESS", cliaddress);
		
		if(invoice.getOrganisation().isVATPayer()) {
			param.put("ISTPVAT","X");
			param.put("P_NOTPVAT","");
		}else {
			param.put("ISTPVAT","");
			param.put("P_NOTPVAT","X");
		}
		
		if(invoice.getOrganisation().isCTPayer()) {
			param.put("P_IS_TC", "X");
			param.put("P_NO_TC", "");
		}else {
			param.put("P_IS_TC", "");
			param.put("P_NO_TC", "X");
		}
		
		if(invoice.getOrganisation().isLVPayer()) {
			param.put("P_IS_PF", "X");
			param.put("P_NO_PF", "");
		}else {
			param.put("P_IS_PF", "");
			param.put("P_NO_PF", "X");
		}
		
		if(client.isVatSubject()) {
			param.put("CLI_ISVAT", "X");
			param.put("P_CLI_NOTVAT", "");
		}else {
			param.put("CLI_ISVAT", "");
			param.put("P_CLI_NOTVAT", "X");
		}
		//param.put("logo", url);
		if(invoice.getPaymentMode().equals("1")) {
			param.put("P_ESPECE_PAY", "X");
			param.put("P_BANQUE_PAY", "");
		}else {
			param.put("P_ESPECE_PAY", "");
			param.put("P_BANQUE_PAY", "X");
		}
		
		param.put("P_INV_CURRENCY", invoice.getCurrency());
		
		
		
		if(null!=invoice.getValidationref() || ""!=invoice.getValidationref()) {
			param.put("INVOICE_NBR", invoice.getValidationref());
		}else {
			param.put("INVOICE_NBR", "....../......");
		}
		if(null!=invoice.getValidatedDate()) param.put("SIGN_DATE" ,this.utilityservice.dateToString(invoice.getValidatedDate(), "dd/MM/yyyy"));
		else param.put("SIGN_DATE" ,".../.../...");
		
		param.put("TOTAL", String.format(Locale.FRANCE, "%,.2f", invoice.getSubtotal()));
		param.put("VAT", String.format(Locale.FRANCE, "%,.2f", invoice.getVatAmount()) );
		param.put("GRAND_TOTAL", String.format(Locale.FRANCE, "%,.2f", invoice.getTotalAmount()) );
		param.put("logo", resourceLoader.getResource("classpath:/static/img/logo.jpg").getFile().toURI().toURL());
		
		//param.put("AMNT_LETTRE", this.frenchNumberToWords.convert(new Double(82287389.0).longValue()));
	
		param.put("AMNT_LETTRE", this.frenchNumberToWords.convert((new Double(Math.round(invoice.getTotalAmount()))).longValue()));
		if(invoice.getSignature()!=null) {
			if(!invoice.getSignature().equals("")) {
				param.put("INV_SIGN", "EBMS ID : "+invoice.getSignature());
			}else {
				param.put("INV_SIGN", "EBMS ID : ");
			}
		}
		String bankAccount = "20294630006/BGF  \n13120-21364280001/BCB \n14449820101-76/BANCOBU";
		param.put("BANK_ACCOUNT", bankAccount);
		
		if(invoice.getComment()!=null) {
			System.out.println("........ : "+invoice.getComment());
			param.put("EXON", "MENTION : "+invoice.getComment());
		}
		//param.put("BANK_ACCOUNT", invoice.getOrganisation().getBankAccount()+"/"+invoice.getOrganisation().getBankName());
		param.put("RAISON",orgName+","+invoice.getOrganisation().getJuridictionForm());
		param.put("TIN",invoice.getOrganisation().getTin());
		
		param.put("RISTOURNE", String.format(Locale.FRANCE, "%,.2f", invoice.getRistourneAmount()));
		if(invoice.getRistourneRate()>0) {
			param.put("lbl_ristourne", "RISTOURNE ("+invoice.getRistourneRate()+"%)");
		}else {
			param.put("lbl_ristourne", "RISTOURNE");
		}
		
		if(invoice.getVatAmount() <=0) {
			param.put("EXON", "MENTION : EXONERE A LA TVA");
		}
		param.put("RC" ,invoice.getOrganisation().getTradeNumber());
		//param.put("ISTPVAT","OUI");
		
		
		param.put("FISCAL" ,invoice.getOrganisation().getFiscalCenter());
		param.put("SECTOR",invoice.getOrganisation().getActivitySecter());
		param.put("JURIS" ,invoice.getOrganisation().getJuridictionForm());
		param.put("TELEPHONE" ,invoice.getOrganisation().getTelephone());
		param.put("P_TITRE", "Nbre de jrs");
		log.info("Status de la facture : "+invoice.getStatus());
		if(invoice.getStatus()==5 || invoice.getStatus()==4 || invoice.getStatus()==0) {
			param.put("CANCELLED_IMG" ,resourceLoader.getResource("classpath:/static/tmp/cancelled.jpg").getFile().toURI().toURL());
		}
		if(invoice.getStatus()==1) {
			param.put("CANCELLED_IMG" ,resourceLoader.getResource("classpath:/static/tmp/unvalidated.jpg").getFile().toURI().toURL());
		}
//		if(client.isVatSubject()) {
//			param.put("CLI_ISVAT", "OUI");
//		}else {
//			param.put("CLI_ISVAT", "NON");
//		}
		param.put("CLI_NAME", client.getName());
		param.put("CLI_TIN", client.getTIN());
		String addr = invoice.getOrganisation().getAddressAvenue()+", "+invoice.getOrganisation().getAddressNumber()+" - " +invoice.getOrganisation().getAddressQuartier();
		param.put("ADDRESS",addr);
		//param.put("SIGN_DATE" ,this.utilityservice.dateToString(invoice.getInvoiceDate(), "dd/MM/yyyy"));
		
		
		param.put("REPR_NAME" ,invoice.getOrganisation().getRepresentativeName());
		param.put("REPR_TITLE" ,invoice.getOrganisation().getRepresentativePosition());
		
		String ui = UUID.randomUUID().toString();
		String filename = ui.replace("-", "");
		JasperPrint printreport = JasperFillManager.fillReport(compiledreport,param ,data);
		byte[] out =JasperExportManager.exportReportToPdf(printreport);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=PDF_"+filename+".pdf");
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(out);
	}
	
	public @ResponseBody ResponseEntity<byte[]> print(Invoice invoice, int avanc) throws IOException, JRException {
		String orgName = invoice.getOrganisation().getName();
		Client client = invoice.getClient();
    	List<PrintArticleDTO> articles = new ArrayList<>();
    	for(ArticleInvoice i: invoice.getArticles()) {
    		PrintArticleDTO p = new PrintArticleDTO();
    		p.setTitle(i.getTitle());
    		p.setPrice(String.format(Locale.FRANCE, "%,.2f", i.getUnityPrice()));
    		p.setQuantity(String.format(Locale.FRANCE, "%,.2f", i.getQuantity()));
    		p.setTotal(String.format(Locale.FRANCE, "%,.2f", i.getTotalNVat()));
    		String unity = i.getUnity()==null?"":i.getUnity();
    		p.setUnity(unity);
    		p.setUnity_price(String.format(Locale.FRANCE, "%,.2f", i.getUnityPrice()));
    		p.setTotalNVAT(String.format(Locale.FRANCE, "%,.2f", i.getTotalNVat()));
    		p.setRubrique(i.getRubricitem().getRubliquename());
    		if(avanc==1) {
    			p.setAvancement(String.format(Locale.FRANCE, "%,.0f", i.getProgressRate()));
    		}
    		else {
    			p.setAvancement("");
    		}
    		articles.add(p);
    	}
		JRBeanCollectionDataSource data = new JRBeanCollectionDataSource(articles);
    	String pathe = "";
    	pathe = resourceLoader.getResource("classpath:static/dir/invoiceLnd.jrxml").getFile().getAbsolutePath();
    	JasperReport compiledreport = JasperCompileManager.compileReport(pathe);
		HashMap<String,Object> param = new HashMap<>();
		
		String lange = LocaleContextHolder.getLocale().toString();
		if(lange.equalsIgnoreCase("en")) {
			param.put("P_TIN", "TIN :");
			param.put("P_ISVATP", "VAT paye :");
			param.put("P_RC", "RC :");
			param.put("P_CENTRE_F", "Tax center : ");
			param.put("P_SECTOR_A", "Activity sector : ");
			param.put("P_INVOICE", "INVOICE");
			param.put("P_IS_TC_P", "Subject to Consumption Tax :");
			param.put("P_IS_PF_P", "Subject to Flat rate levy :");
			param.put("P_YES", "YES");
			param.put("P_NO", "NO");
			param.put("P_BANK_LBL","Bank account : ");
			param.put("P_PERS_PHYS_P", "Physical person");
			param.put("P_SOCIETE_P", "Company");
			//param.put("P_COMMENT", "• All required tours of fumigation as per LPO have been accomplished. ");
			param.put("P_COMMENT", "");
			param.put("P_THANKS", "Thank you for your business. It’s was a pleasure to work with you.");
			param.put("P_SINCER", "Sincerely yours,");
			param.put("P_TERMS", "Terms : Payment does not exceed 7 Days after service. ");
			param.put("P_LBL_NATURE","Service");
			param.put("P_LBL_REFERENCE","Reference");
			param.put("P_LBL_QTY", "Qty");
			param.put("P_LBL_UNIT_PRICE", "Unit Price");
			param.put("P_LBL_PVHTVA", "Subtotal");
			param.put("P_LBL_RISTOURNE", "Discount");
			param.put("P_LBL_VAT", "Tax");
			param.put("P_LBL_TVAC", "Total");
			param.put("P_PS", "Comments");
			param.put("P_PYT_KIND", "Kind of payment");
			param.put("P_PYT_CASH", "Cash");
			param.put("P_PYT_BANK", "Bank");
		}else {
			//label
			param.put("P_TIN", "NIF :");
			param.put("P_ISVATP", "Assujetti à la TVA :");
			param.put("P_RC", "RC :");
			param.putIfAbsent("P_CENTRE_F", "Centre fiscal : ");
			param.put("P_SECTOR_A", "Secteur d'activités : ");
			param.put("P_INVOICE", "FACTURE");
			param.put("P_IS_TC_P", "Assujetti à la TC :");
			param.put("P_IS_PF_P", "Assujetti à la PF :");
			param.put("P_YES", "OUI");
			param.put("P_NO", "NON");
			param.put("P_BANK_LBL","Compte bancaire : ");
			param.put("P_PERS_PHYS_P", "Personne physique ");
			param.put("P_SOCIETE_P", "Société");	
			param.put("P_COMMENT", "");
			//param.put("P_COMMENT", "• Tous les tours de fumigations, convenus dans le LPO ont été effectués. ");
			param.put("P_THANKS", "Nous vous remercions de nous avoir fait confiance. C'était un réel plaisir de travailler avec vous.");
			param.put("P_SINCER", "Cordialement,"); 
			param.put("P_TERMS", "Conditions : Le paiement doit être fait au maximum dans les 7 jours après L'éxecution du service.  ");
			param.put("P_LBL_NATURE","Nature du service");
			param.put("P_LBL_REFERENCE","Référence");
			param.put("P_LBL_QTY", "Qté");
			param.put("P_LBL_UNIT_PRICE", "PU");
			param.put("P_LBL_PVHTVA", "PVHTVA");
			param.put("P_LBL_RISTOURNE", "Ristourne");
			param.put("P_LBL_VAT", "TVA");
			param.put("P_LBL_TVAC", "TVAC");
			param.put("P_PS", "Commentaires");
			param.put("P_PYT_KIND", "Type de paiement");
			param.put("P_PYT_CASH", "Espèces");
			param.put("P_PYT_BANK", "Banques");
		}
		if(client.getTypeClient()=="entr") {
			param.put("P_CLI_NOTSOC", "");
			param.put("P_CLI_ISSOC", "X");
		}else {
			param.put("P_CLI_NOTSOC", "X");
			param.put("P_CLI_ISSOC", "");
		}
		
		
		param.put("P_PHYSIQUE", "X");
		
		
		param.put("P_ADDR_AV_NUM", invoice.getOrganisation().getAddressAvenue()+" n."+invoice.getOrganisation().getAddressNumber());
		param.put("P_ADDR_COM_QUARTIER", invoice.getOrganisation().getAddressCommune()+", "+invoice.getOrganisation().getAddressQuartier());
		
		//param.put("P_CLI_ADDRESS", invoice.getDelivaryAddress());
		String cliaddress = invoice.getClient().getProvince()+", "+invoice.getClient().getCommune()+", "+ invoice.getClient().getQuartier();
				
		param.put("P_CLI_ADDRESS", cliaddress);
		
		if(invoice.getOrganisation().isVATPayer()) {
			param.put("ISTPVAT","X");
			param.put("P_NOTPVAT","");
		}else {
			param.put("ISTPVAT","");
			param.put("P_NOTPVAT","X");
		}
		
		if(invoice.getOrganisation().isCTPayer()) {
			param.put("P_IS_TC", "X");
			param.put("P_NO_TC", "");
		}else {
			param.put("P_IS_TC", "");
			param.put("P_NO_TC", "X");
		}
		
		if(invoice.getOrganisation().isLVPayer()) {
			param.put("P_IS_PF", "X");
			param.put("P_NO_PF", "");
		}else {
			param.put("P_IS_PF", "");
			param.put("P_NO_PF", "X");
		}
		
		if(client.isVatSubject()) {
			param.put("CLI_ISVAT", "X");
			param.put("P_CLI_NOTVAT", "");
		}else {
			param.put("CLI_ISVAT", "");
			param.put("P_CLI_NOTVAT", "X");
		}
		//param.put("logo", url);
		if(invoice.getPaymentMode().equals("1")) {
			param.put("P_ESPECE_PAY", "X");
			param.put("P_BANQUE_PAY", "");
		}else {
			param.put("P_ESPECE_PAY", "");
			param.put("P_BANQUE_PAY", "X");
		}
		
		param.put("P_INV_CURRENCY", invoice.getCurrency());
		
		if(invoice.getSignature()!=null) {
			if(!invoice.getSignature().equals("")) {
				param.put("INV_SIGN", "EBMS ID : "+invoice.getSignature());
			}else {
				param.put("INV_SIGN", "EBMS ID : ");
			}
		}
		
		if(null!=invoice.getValidationref() || ""!=invoice.getValidationref()) {
			param.put("INVOICE_NBR", invoice.getValidationref());
		}else {
			param.put("INVOICE_NBR", "....../......");
		}
		if(null!=invoice.getValidatedDate()) param.put("SIGN_DATE" ,this.utilityservice.dateToString(invoice.getValidatedDate(), "dd/MM/yyyy"));
		else param.put("SIGN_DATE" ,".../.../...");
		
		
		
		param.put("TOTAL", String.format(Locale.FRANCE, "%,.2f", invoice.getSubtotal()));
		param.put("VAT", String.format(Locale.FRANCE, "%,.2f", invoice.getVatAmount()) );
		param.put("GRAND_TOTAL", String.format(Locale.FRANCE, "%,.2f", invoice.getTotalAmount()) );
		param.put("logo", resourceLoader.getResource("classpath:/static/img/logo.jpg").getFile().toURI().toURL());
		param.put("AMNT_LETTRE", this.frenchNumberToWords.convert((new Double(Math.round(invoice.getTotalAmount()))).longValue()));
		
		String bankAccount = "20294630006/BGF  \n13120-21364280001/BCB \n14449820101-76/BANCOBU";
		param.put("BANK_ACCOUNT", bankAccount);
		
		if(invoice.getComment()!=null) {
			if(!invoice.getComment().isEmpty()) {
				System.out.println("........ : "+invoice.getComment());
				param.put("EXON", "MENTION : "+invoice.getComment());
			}
		}
		
		param.put("RISTOURNE", String.format(Locale.FRANCE, "%,.2f", invoice.getRistourneAmount()));
		if(invoice.getRistourneRate()>0) {
			param.put("lbl_ristourne", "RISTOURNE ("+invoice.getRistourneRate()+"%)");
		}else {
			param.put("lbl_ristourne", "RISTOURNE");
		}
		param.put("P_INV_CURRENCY", "BIF");
		param.put("RAISON",orgName+","+invoice.getOrganisation().getJuridictionForm());
		param.put("TIN",invoice.getOrganisation().getTin());
		param.put("RC" ,invoice.getOrganisation().getTradeNumber());
		param.put("FISCAL" ,invoice.getOrganisation().getFiscalCenter());
		param.put("SECTOR",invoice.getOrganisation().getActivitySecter());
		param.put("JURIS" ,invoice.getOrganisation().getJuridictionForm());
		param.put("TELEPHONE" ,invoice.getOrganisation().getTelephone());
		param.put("P_TITRE", "Nbre de jrs");
		log.info("Status de la facture : "+invoice.getStatus());
		if(invoice.getStatus()==5 || invoice.getStatus()==4 || invoice.getStatus()==0) {
			param.put("CANCELLED_IMG" ,resourceLoader.getResource("classpath:/static/tmp/cancelled.jpg").getFile().toURI().toURL());
		}
		if(invoice.getStatus()==1) {
			param.put("CANCELLED_IMG" ,resourceLoader.getResource("classpath:/static/tmp/unvalidated.jpg").getFile().toURI().toURL());
		}
		
		param.put("CLI_NAME", client.getName());
		param.put("CLI_TIN", client.getTIN());
		String addr = invoice.getOrganisation().getAdress();
		param.put("ADDRESS",addr);
		param.put("REPR_NAME" ,invoice.getOrganisation().getRepresentativeName());
		param.put("REPR_TITLE" ,invoice.getOrganisation().getRepresentativePosition());
		String ui = UUID.randomUUID().toString();
		String filename = ui.replace("-", "");
		JasperPrint printreport = JasperFillManager.fillReport(compiledreport,param ,data);
		byte[] out =JasperExportManager.exportReportToPdf(printreport);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=PDF_"+filename+".pdf");
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(out);
	}
}
