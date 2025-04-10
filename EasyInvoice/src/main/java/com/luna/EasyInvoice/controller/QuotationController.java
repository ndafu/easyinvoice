package com.luna.EasyInvoice.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
//import com.github.royken.converter.FrenchNumberToWords;

import com.luna.EasyInvoice.entities.ArticleQuotation;
import com.luna.EasyInvoice.entities.ArticleReference;
import com.luna.EasyInvoice.entities.Client;
import com.luna.EasyInvoice.entities.InvoiceNumbering;
import com.luna.EasyInvoice.entities.Organisation;
import com.luna.EasyInvoice.entities.Quotation;
import com.luna.EasyInvoice.entities.QuotationNumbering;
import com.luna.EasyInvoice.pojo.ArticleQPojo;
import com.luna.EasyInvoice.pojo.OrganisationPojo;
import com.luna.EasyInvoice.pojo.QuotationPojo;
import com.luna.EasyInvoice.pojo.SalesGeneratorPojo;
import com.luna.EasyInvoice.service.QuotationNumberingService;
import com.luna.EasyInvoice.service.implementation.ArticleReferenceServImpl;
import com.luna.EasyInvoice.service.implementation.ArticleServImpl;
import com.luna.EasyInvoice.service.implementation.CartServiceImpl;
import com.luna.EasyInvoice.service.implementation.ClientServImpl;
import com.luna.EasyInvoice.service.implementation.OrganisationServImpl;
import com.luna.EasyInvoice.service.implementation.QuotationNumberingServiceImpl;
import com.luna.EasyInvoice.service.implementation.QuotationServiceImpl;
import com.luna.EasyInvoice.service.implementation.TaxServImpl;
import com.luna.EasyInvoice.service.implementation.UserPrincipal;
import com.luna.EasyInvoice.utility.FrenchNumberToWords;
import com.luna.EasyInvoice.utility.Response;
import com.luna.EasyInvoice.utility.UtilitiesService;

@Controller
public class QuotationController {

	@Autowired
	ClientServImpl clientservice;
	
	@Autowired
	CartServiceImpl cartservice;
	
	@Autowired
	TaxServImpl taxservice;
	
	@Autowired
	Environment env;
	
	@Autowired
	ArticleReferenceServImpl articlereferenceservice;
	
	@Autowired
	UtilitiesService utilitiesService;
	
	@Autowired
	QuotationServiceImpl quotationservice;
	
	@Autowired
	ArticleServImpl articleservice;
	
	@Autowired
	OrganisationServImpl organisationservice;
	
	@Autowired
	FrenchNumberToWords frenchNumberToWords;
	
	@Autowired
	QuotationNumberingServiceImpl quotationnumberingservice;
	
	@PreAuthorize("hasAnyRole('COM_MANAGER')")
	@GetMapping("/quotation")
	public String index(Model model, HttpServletRequest request) {
		return this.index(1,model, request);
	}
	
	@GetMapping("/quotations")
	public String display(Model model) {
		return this.display(1, model);
	}
	@GetMapping("/quotations/{pageno}")
	public String display(@PathVariable() int pageno, Model model) {
		int pagesize = Integer.valueOf(this.env.getProperty("config.pagesize"));
//		List<Organisation> organisations = this.organisationservice.findOrganisation();
		Page<Quotation> quotations = this.quotationservice.fetch(pageno, pagesize);
		
		model.addAttribute("quotations", quotations.getContent());
		model.addAttribute("currentPage", pageno);
		model.addAttribute(quotations);
//		model.addAttribute("organisation", this.utilitiesService.getOrganisationFromConnectedUser());
		model.addAttribute("jsName", "super.js"); //super.js
		model.addAttribute("totalpages",quotations.getTotalPages());
		model.addAttribute("size", pagesize);
		return "invoice/quotations";
	}
	
	@GetMapping("/quotation/{pageno}")
	public String index(@PathVariable() int pageno, Model model, HttpServletRequest request) {
		List<SalesGeneratorPojo> carts = (List<SalesGeneratorPojo>) request.getSession().getAttribute("SALES_SESSION");
		
		System.out.println("Carts : "+carts);
		if(carts != null)  {
			double vat = 0.0;
			double totalWvat = 0.0;
			double totalVatInc= 0.0;
			double total = 0.0;
			for(SalesGeneratorPojo ca:carts) {
				vat = vat + ca.getTotalVAT();
				totalWvat = totalWvat + ca.getTotalNVAT();
				totalVatInc = totalVatInc + ca.getTotalIncVAT();
			}
			if(vat>0) {
				model.addAttribute("tax_mod", 2);
			}
			model.addAttribute("tax",0.0);
			model.addAttribute("total_wvat", totalWvat);
			model.addAttribute("total_vat", vat);
			model.addAttribute("total_vatinc", totalVatInc);
			model.addAttribute("quotations", carts);
		}
		model.addAttribute("articles", this.articlereferenceservice.findArticle(true));
		model.addAttribute("clients", this.clientservice.findClient());
		model.addAttribute("taxs", this.taxservice.getActiveTaxList());
		model.addAttribute("jsName", "super.js"); //super.js
		return "invoice/quotation";
	}
	
	
	@PostMapping("/cartManagement")
	public @ResponseBody Response cartManager(
			@RequestParam String quantity, 
			@RequestParam String art,
			@RequestParam String art_desc,
			@RequestParam String unity_price,
			@RequestParam String art_tax,
			@RequestParam String isTaxable,
			@RequestParam String art_is_vat,
			@RequestParam String art_total,
			HttpServletRequest request
			) {
		Response rsp = new Response();
		int coef = 1;
		if(isTaxable.equals("2")) {
			coef= 1;
		}else {
			coef= 0;
		}
		//Check the articles		
		ArticleReference articlereference = this.articlereferenceservice.findArticle(Long.valueOf(art));
		SalesGeneratorPojo pojo = new SalesGeneratorPojo();
		
		if(articlereference!=null) {
			pojo.setDesignation(articlereference.getDescription());
			pojo.setId(articlereference.getId());
			pojo.setQuantity(Double.valueOf(quantity));
			pojo.setTax(Double.valueOf(art_tax));
			pojo.setTitle(articlereference.getTitle());
			pojo.setUnity_price(Double.valueOf(unity_price));
			pojo.setTotalNVAT(pojo.getUnity_price()*pojo.getQuantity());
			pojo.setTotalVAT(pojo.getTotalNVAT()*pojo.getTax()/100*coef);
			pojo.setTotalIncVAT(pojo.getTotalNVAT()+pojo.getTotalVAT());
		}
		List<SalesGeneratorPojo> carts = (List<SalesGeneratorPojo>) request.getSession().getAttribute("SALES_SESSION");
		//check if carts is present in session or not
		if(carts == null) {
			carts = new ArrayList<>();
			carts.add(pojo);
			request.getSession().setAttribute("SALES_SESSION", carts);
		}else {
			//check if sales existed before
			if(this.cartservice.isArticleListed(carts, pojo)) {
				carts = this.cartservice.updateArticleList(carts, pojo);
			}else {
				carts.add(pojo);
			}
			request.getSession().setAttribute("SALES_SESSION", carts);
		}
		rsp.setCode(1);
		rsp.setDescription("Success");
		
		List<Object> bod = new ArrayList<>();
		for(SalesGeneratorPojo ge : carts) {
			bod.add(ge);
		}
		rsp.setBody(bod);
		
		return rsp;
	}
	
	@PostMapping("/deleteElementcart")
	public @ResponseBody Response delete(@RequestParam String id, HttpServletRequest request) {
		Response response = new Response();
		List<SalesGeneratorPojo> carts = (List<SalesGeneratorPojo>) request.getSession().getAttribute("SALES_SESSION");
		System.out.println("Size : "+carts.size());
		if(carts.size()>0) {
			int row = 0;
			for(SalesGeneratorPojo sal : carts) {
				if(sal.getId().compareTo(Long.valueOf(id))==0) {
					carts.remove(row);
					break;
				}
				row++;
			}
			request.getSession().setAttribute("SALES_SESSION", carts);
			response.setCode(1);
			response.setDescription("Success");
			List<Object> body = new ArrayList<>();
			
			for(SalesGeneratorPojo ge : carts) {
				body.add(ge);
			}
			response.setBody(body);
		}
		return response;
	}
	
	@PostMapping("/saveQuotation")
	@Transactional
	public @ResponseBody Response save(
				HttpServletRequest request, 
				@RequestParam String quo_client,
				@RequestParam String quo_date,
				@RequestParam String quo_sub_tot, 
				@RequestParam String quo_tax,
				@RequestParam String quo_total,
				@RequestParam String quo_currency,
				@RequestParam String tax_rate, 
				@RequestParam String quo_taxe,
				@RequestParam String quo_destination, 
				@RequestParam String quo_address,
				@RequestParam String quo_comment
			) {
		Response response = new Response();
		Quotation quotation = new Quotation();
		//System.out.println("Tax mesuring : "+quo_tax);
		UserPrincipal princ = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();  
		String createdBy = princ.getUserName();
		Client client = this.clientservice.findSingleClient(Long.valueOf(quo_client));
		quotation.setCreatedBy(createdBy);
		quotation.setCreatedOn(new Date());
		quotation.setClient(client);
		quotation.setComments(quo_comment);
		quotation.setDelivaryAddress(quo_destination);
		quotation.setGrandTotal(Double.valueOf(quo_total.replace(" ", "")));
		quotation.setInvoiceAddress(quo_address);
		quotation.setQuotationDate(this.utilitiesService.getdateFromString(quo_date));
		quotation.setStatus(1);
		quotation.setTaxApplicable(quo_taxe);
		quotation.setTotalTax(0);		
		quotation.setSubTotal(Double.valueOf(quo_sub_tot.replace(" ", "")));
		if(quo_taxe.equals("2")) {
			quotation.setTotalTax(Double.valueOf(quo_tax.replace(" ", "")));
		}else {
			quotation.setTotalTax(0.0);
		}
		quotation.setCurrency(quo_currency);
		quotation.setReference(this.getReference(this.utilitiesService.getOrganisationFromConnectedUser().getId()));
		//save quotation
		quotation.setOrganisation(this.utilitiesService.getOrganisationFromConnectedUser());
		this.quotationservice.save(quotation);
		List<SalesGeneratorPojo> carts = (List<SalesGeneratorPojo>) request.getSession().getAttribute("SALES_SESSION");
		//List<ArticleQuotation> articles = new ArrayList<>();
		for(SalesGeneratorPojo sales : carts) {
			ArticleQuotation art = new ArticleQuotation();
			art.setDescription(sales.getDesignation());
			art.setQuantity(sales.getQuantity());
			art.setQuotation(quotation);
			//art.setTax(sales.getTax());
			
			art.setTitle(sales.getTitle());
			
			art.setTotalNVAT(sales.getTotalNVAT());
			art.setTotalVAT(sales.getTotalVAT());
			art.setTax(sales.getTotalVAT());
			art.setTaxRate(sales.getTax());
			
			art.setTotal(sales.getTotalIncVAT());
			
			art.setUnityPrice(sales.getUnity_price());
			art.setTaxMiseasure("%");
			//articles.add(art);
			this.articleservice.saveArticle(art);
		}
		response.setCode(1);
		response.setDescription("Success");
		request.getSession().setAttribute("SALES_SESSION", null);
		return response;
	}
	
	@PostMapping("/taxCalcul")
	public @ResponseBody Response calculTax(HttpServletRequest request, @RequestParam String typ) {
		Response response = new Response();
		List<SalesGeneratorPojo> carts = (List<SalesGeneratorPojo>) request.getSession().getAttribute("SALES_SESSION");
		System.out.println("Type : "+typ);
		if(carts!=null) {
			if(carts.size()>0) {
				int coefficient = 1;
				if(typ.equalsIgnoreCase("2")) {
					coefficient=1;
				}else {
					coefficient=0;
				}
				for(SalesGeneratorPojo sal : carts) {
					sal.setTotalVAT(coefficient*sal.getTotalNVAT()*sal.getTax()/100);
					sal.setTotalIncVAT(sal.getTotalNVAT()+sal.getTotalVAT());			
				}
				response.setCode(1);
				response.setDescription("Success");
				
				List<Object> bod = new ArrayList<>();
				for(SalesGeneratorPojo ge : carts) {
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
	
	@PostMapping("/searchQuotation")
	public @ResponseBody Response search(@RequestParam String id) {
		
		//System.out.println("Test ........");
		Response response = new Response();
		Quotation quotation = this.quotationservice.fetch(Long.valueOf(id));
		QuotationPojo quo = new QuotationPojo();
		List<ArticleQPojo> articlep = new ArrayList<>();
		for(ArticleQuotation art : quotation.getArticlesQuotation()) {
			ArticleQPojo ar = new ArticleQPojo();
			ar.setDescription(art.getDescription());
			ar.setQuantity(art.getQuantity());
			ar.setTitle(art.getTitle());
			ar.setTotalNVAT(art.getTotalNVAT());
			ar.setTotalVat(art.getTotalVAT());
			ar.setTotalWVAT(art.getTotal());
			ar.setVat(art.getTaxRate());
			//ar.setTotal(art.getTotal());
			ar.setUnityPrice(art.getUnityPrice());
			articlep.add(ar);
		}
		quo.setArticles(articlep);
		quo.setCurrency(quotation.getCurrency());	
		if(quotation.getTaxApplicable().equals("2")) {
			quo.setIsTaxable("TVA APPLICABLE");
		}else {
			quo.setIsTaxable("TVA EXONEREE");
		}
		quo.setReference(quotation.getReference());
		quo.setClient(quotation.getClient());
		quo.setComments(quotation.getComments());
		quo.setDelivaryAddress(quotation.getDelivaryAddress());
		quo.setGrandTotal(quotation.getGrandTotal());
		quo.setId(quotation.getId());
		quo.setInvoiceAddress(quotation.getInvoiceAddress());
		quo.setQuotationDate(quotation.getQuotationDate());
		quo.setStatus(quotation.getStatus());
		quo.setSubTotal(quotation.getSubTotal());
		quo.setTotalTax(quotation.getTotalTax());
		
		OrganisationPojo org = new OrganisationPojo();
		Organisation organisation = quotation.getOrganisation();
		if(organisation!=null) {
			org.setAddressAvenue(organisation.getAddressAvenue());
			org.setAddressCommune(organisation.getAddressCommune());
			org.setAddressNumber(organisation.getAddressNumber());
			org.setAddressProvince(organisation.getAddressProvince());
			org.setAddressQuartier(organisation.getAddressQuartier());
			org.setAdress(organisation.getAdress());
			org.setLogo(organisation.getLogo());
			org.setName(organisation.getName());
			org.setTelephone(organisation.getTelephone());
			org.setActivitySecter(organisation.getActivitySecter());
			org.setFiscalCenter(organisation.getFiscalCenter());
			org.setJuridictionForm(organisation.getJuridictionForm());
			org.setPostalNumber(organisation.getPostalNumber());
			org.setTin(organisation.getTin());
			org.setTradeNumber(organisation.getTradeNumber());
			org.setVATPayer(organisation.isVATPayer());
			
			quo.setOrganisation(org);
		}
		List<Object> objects = new ArrayList<>();
		response.setCode(1);
		response.setDescription("SUCCESS");
		objects.add(quo);
		response.setBody(objects);
		return response;
	}
	
	@GetMapping("/printQ")
	public String printQuotation(Model model) {
		return this.printQuotation(1,model);
	}
	
	
	@GetMapping("/printQ/{qRef}")
	public String printQuotation(@PathVariable() int qRef, Model model) {
		Organisation organisations = this.organisationservice.findSingleOrganisation(Long.valueOf("1"));
		Quotation quotation = this.quotationservice.fetch(Long.valueOf(qRef));
		//List<ArticleQuotation> articles = quotation.getArticlesQuotation();
		double pvhtva = 0.0;
		double tva = 0.0;
		
		model.addAttribute("moneyInLetter", frenchNumberToWords.convert((new Double(quotation.getGrandTotal())).longValue()));
		model.addAttribute("size", quotation.getArticlesQuotation().size());
		model.addAttribute("pvhtva", pvhtva);
		model.addAttribute("tva", tva);
		model.addAttribute("currentDay",this.utilitiesService.dateToString(new Date(), "dd/MM/yyyy"));
		model.addAttribute("quotation", quotation);
		model.addAttribute("organisation", organisations);
		model.addAttribute("jsName", "super.js"); //super.js
		return "invoice/quotationPrintable";
	}
	
	private String getReference(Long org_id) {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		List<QuotationNumbering> num = this.quotationnumberingservice.find(year,org_id);
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
	
	@PostMapping("/deleteQuotation")
	public @ResponseBody Response deleteQuotation(@RequestParam String id) {
		Response response = new Response();
		Quotation quotation = this.quotationservice.fetch(Long.valueOf(id));
		if(quotation.getStatus()>1) {
			response.setCode(-1);
		}else {
			quotation.setStatus(0);
			this.quotationservice.save(quotation);
			response.setCode(1);
		}
		return response;
	}
}
