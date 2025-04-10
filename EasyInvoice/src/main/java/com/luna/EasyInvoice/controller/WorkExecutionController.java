package com.luna.EasyInvoice.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.jfree.util.Log;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.luna.EasyInvoice.dto.ArticleSessionDTO;
import com.luna.EasyInvoice.dto.ItemDTO;
import com.luna.EasyInvoice.dto.ItemSessionDTO;
import com.luna.EasyInvoice.dto.RubriqueDTO;
import com.luna.EasyInvoice.entities.Article;
import com.luna.EasyInvoice.entities.ArticleInvoice;
import com.luna.EasyInvoice.entities.ArticleOrder;
import com.luna.EasyInvoice.entities.ArticleQuotation;
import com.luna.EasyInvoice.entities.Client;
import com.luna.EasyInvoice.entities.Invoice;
import com.luna.EasyInvoice.entities.InvoiceNumbering;
import com.luna.EasyInvoice.service.implementation.ArticleServImpl;
import com.luna.EasyInvoice.entities.Organisation;
import com.luna.EasyInvoice.entities.Quotation;
import com.luna.EasyInvoice.entities.RubricItem;
import com.luna.EasyInvoice.entities.WorkExecution;
import com.luna.EasyInvoice.entities.WorkRubric;
import com.luna.EasyInvoice.pojo.ArticleInvoicePojo;
import com.luna.EasyInvoice.service.implementation.ClientServImpl;
import com.luna.EasyInvoice.service.implementation.InvoiceNumberingServiceImpl;
import com.luna.EasyInvoice.service.implementation.InvoiceServiceImpl;
import com.luna.EasyInvoice.service.implementation.RubricItemService;
import com.luna.EasyInvoice.service.implementation.WorkExecutionServiceImpl;
import com.luna.EasyInvoice.service.implementation.WorkRubricServiceImpl;
import com.luna.EasyInvoice.utility.Response;
import com.luna.EasyInvoice.utility.UtilitiesService;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class WorkExecutionController {
	
	final private ClientServImpl clientservice;
	final private UtilitiesService utilitieservice;
	final private WorkExecutionServiceImpl workexecutifservice;
	final private WorkRubricServiceImpl workrubricsserviceimpl;
	final private RubricItemService rubricitemservice;
	final private InvoiceNumberingServiceImpl invoicenumberingservice;
	final private InvoiceServiceImpl invoiceserviceimpl;
	final private ArticleServImpl articleserviceimpl;

	@GetMapping("/orderExec") 
	public String index(Model model, HttpServletRequest request) {
		
		model.addAttribute("message","Welcome");
		model.addAttribute("clients", this.clientservice.findClient());
		model.addAttribute("jsName", "ordExec.js"); //super.js
		
		String currency = (String) request.getSession().getAttribute("CURR_SES");
		if(currency!=null) {
			model.addAttribute("currency", currency);
		}else {
			model.addAttribute("currency", "");
		}
		
		String clientsession = (String) request.getSession().getAttribute("CLI_SES");
		if(clientsession!=null) {
			model.addAttribute("clientsession", Long.valueOf(clientsession));
		}else {
			model.addAttribute("clientsession", "");
		}
		String taxsession = (String) request.getSession().getAttribute("TAX_SES");
		if(taxsession!=null) {
			model.addAttribute("taxsession", taxsession);
		}else {
			model.addAttribute("taxsession", "");
		}
		String typesession = (String) request.getSession().getAttribute("TYP_SES");
		model.addAttribute("typesession", typesession);
		
		List<RubriqueDTO> rubriques = (List<RubriqueDTO>) request.getSession().getAttribute("RUBRIQUE_SES");
		if(rubriques!=null) {
			model.addAttribute("rubriques", rubriques);
		}
		List<ArticleSessionDTO> articleses = (List<ArticleSessionDTO>) request.getSession().getAttribute("ARTICLE_SES");
		if(articleses!=null) {
			double grandTotal = 0.0;
			double subtotal = 0.0;
			double totTax = 0.0;
			for(ArticleSessionDTO art : articleses) {
				subtotal = subtotal + art.getArt_total_nvat();
				grandTotal = grandTotal +art.getArt_total();
				totTax = totTax + art.getTax_amount();
			}
			
			model.addAttribute("sub", subtotal);
			model.addAttribute("tax", totTax);
			model.addAttribute("total", grandTotal);
			model.addAttribute("articlesession", articleses);
		}
		return "works/workexec";
	}
	
	
	@PostMapping("/manageRubriqueSession")
	public @ResponseBody Response manageRubriqueSession(@RequestParam String rubriquename,  HttpServletRequest request) {
		Response response = new Response();
		RubriqueDTO rubrique = new RubriqueDTO();
		rubrique.setName(rubriquename.trim());
		//List<RubriqueDTO> rubriques = (List<RubriqueDTO>) request.getSession().getAttribute("RUBRIQUE_SES");
		if(request.getSession().getAttribute("RUBRIQUE_SES")==null) {
			List<RubriqueDTO> rubriques = new ArrayList<>();
			rubrique.setNum(Long.valueOf(1));
			rubriques.add(rubrique);
			request.getSession().setAttribute("RUBRIQUE_SES", rubriques);
			response.setCode(1);
			response.setDescription("Success");
			List<Object> obj = new ArrayList<>();
			obj.add(rubrique);
			response.setBody(obj);
		}else {
			List<RubriqueDTO> cartse = (List<RubriqueDTO>) request.getSession().getAttribute("RUBRIQUE_SES");
			boolean isPresent = false;
			for(RubriqueDTO a : cartse) {
				if(a.getName().trim().equals(rubriquename)) {
					isPresent=true;
				}
			}
			if(isPresent) {
				List<Object> obj = new ArrayList<>();
				for(RubriqueDTO arti : cartse) {
					obj.add(arti);
				}
				response.setBody(obj);
				response.setCode(2);
				response.setDescription("LA RUBRIQUE EST DEJA EXISTANTE DANS LA LISTE DES RUBRIQUES A CREER POUR CE PROJET. PRIERE DE BIEN VOULOIR LA SUPPRIMER D'ABORD AVANT DE L'AJOUTER.");
				
			}else {
				List<RubriqueDTO> arts = (List<RubriqueDTO>) cartse;
				rubrique.setNum(Long.valueOf(cartse.size()+1));
				arts.add(rubrique);
				request.getSession().setAttribute("RUBRIQUE_SES", arts);
				cartse = (List<RubriqueDTO>) request.getSession().getAttribute("RUBRIQUE_SES");
				response.setCode(1);
				List<Object> obj = new ArrayList<>();
//				for(RubriqueDTO arti : cartse) {
//					
//				}
				obj.add(rubrique);
				response.setDescription("SUCCESS");
				response.setBody(obj);
			}
		}
		Log.debug(response);
		return response;
	}
	
	@PostMapping(value= {"/deleteElementOnList"})
	public @ResponseBody Response deleteElement(@RequestParam String reference, @RequestParam String rubrique, HttpServletRequest request) {
		Response response = new Response();
		List<ArticleSessionDTO> newSessionarticleses = new ArrayList<ArticleSessionDTO>();
		List<ArticleSessionDTO> articleses = (List<ArticleSessionDTO>) request.getSession().getAttribute("ARTICLE_SES");
		for(ArticleSessionDTO a : articleses) {
			if(a.getArticle().trim().equals(reference) && a.getRubliqueid().equals(rubrique)) {
			}else {
				newSessionarticleses.add(a);
			}
		}
		request.getSession().setAttribute("ARTICLE_SES", null);
		request.getSession().setAttribute("ARTICLE_SES",newSessionarticleses);
		List<Object> obj = new ArrayList<>();
		obj.add(newSessionarticleses);
		response.setBody(obj);
		response.setCode(1);
		return response;
	}
	
	@PostMapping("/sessionCurrencyManage")
	public @ResponseBody Response currencySessionManage(@RequestParam String currency, HttpServletRequest request) {
		Response response = new Response();
		String currencySession = (String) request.getSession().getAttribute("CURR_SES");
		if(currencySession!=null) {
			request.getSession().setAttribute("CURR_SES", null);
			request.getSession().setAttribute("CURR_SES", currency);
		}else {
			request.getSession().setAttribute("CURR_SES", currency);
		}
//		System.out.println("Currency session : "+request.getSession().getAttribute("CURR_SES"));
		return response;
	}
	
	@PostMapping("/sessionTaxManage")
	public @ResponseBody Response manageTaxSession(@RequestParam String tax, HttpServletRequest request) {
		Response response = new Response();
		
		/*
		 * 	request.getSession().setAttribute("CURR_SES",null);
			request.getSession().setAttribute("TAX_SES",null);
			request.getSession().setAttribute("CLI_SES",null);
			request.getSession().setAttribute("TYP_SES",null);
		 */
		String taxsession = (String) request.getSession().getAttribute("TAX_SES");
		if(taxsession!=null) {
			request.getSession().setAttribute("TAX_SES", null);
			request.getSession().setAttribute("TAX_SES", tax);
		}else {
			request.getSession().setAttribute("TAX_SES", tax);
		}
		return response;
	}
	
	@PostMapping("/sessionClientManage")
	public @ResponseBody Response manageClientSession(@RequestParam String client, HttpServletRequest request) {
		Response response = new Response();
		String clientsession = (String) request.getSession().getAttribute("CLI_SES");
		if(clientsession!=null) {
			request.getSession().setAttribute("CLI_SES", null);
			request.getSession().setAttribute("CLI_SES", client);
		}else {
			request.getSession().setAttribute("CLI_SES", client);
		}
		return response;
	}
	
	@PostMapping("/sessionTypeManage")
	public @ResponseBody void manageTypeSession(@RequestParam String type, HttpServletRequest request) {
		String typesession = (String) request.getSession().getAttribute("TYP_SES");
		if(typesession!=null) {
			request.getSession().setAttribute("TYP_SES", null);
			request.getSession().setAttribute("TYP_SES", type);
		}else {
			request.getSession().setAttribute("TYP_SES", type);
		}
	}
	
	@PostMapping("/manageArticleSession")
	public @ResponseBody Response saveArticleSession(@RequestParam String rubrique,
			@RequestParam String article,
			@RequestParam String unity,
			@RequestParam String quantity,
			@RequestParam String unity_price,
			@RequestParam String tax,
			@RequestParam String tax_amount,
			@RequestParam String art_total_nvat,
			@RequestParam String art_total,
			HttpServletRequest request
			) {
		Response response = new Response();
		List<RubriqueDTO> rubriques = (List<RubriqueDTO>) request.getSession().getAttribute("RUBRIQUE_SES");
		System.out.println("Rubrique recu : "+rubrique);
		System.out.println("Rubrique session : "+rubriques);
		
		ArticleSessionDTO art = new ArticleSessionDTO();
		String rub_name = "";
		for(RubriqueDTO rub : rubriques) {
			if(rub.getNum().toString().equals(rubrique)) {
				rub_name = rub.getName();
				break;
			}
		}
		art.setRubliquename(rub_name);
		art.setArt_total(Double.valueOf(art_total));
		art.setArt_total_nvat(Double.valueOf(art_total_nvat));
		art.setArticle(article);
		art.setQuantity(Double.valueOf(quantity));
		art.setRubliqueid(rubrique);
		art.setTax(Double.valueOf(tax));
		art.setUnity(unity);
		art.setTax_amount(Double.valueOf(tax_amount));
		art.setUnity_price(Double.valueOf(unity_price));
		
		if(request.getSession().getAttribute("ARTICLE_SES")==null) {
			List<ArticleSessionDTO> articles = new ArrayList<>();
			articles.add(art);
			request.getSession().setAttribute("ARTICLE_SES", articles);
			response.setCode(1);
			response.setDescription("Success");
			List<Object> obj = new ArrayList<>();
			obj.add(articles);
			response.setBody(obj);
		}else {
			List<ArticleSessionDTO> articleses = (List<ArticleSessionDTO>) request.getSession().getAttribute("ARTICLE_SES");
			boolean isPresent = false;
			for(ArticleSessionDTO a : articleses) {
				if(a.getArticle().trim().equals(article) && a.getRubliqueid().equals(rubrique)) {
					isPresent=true;
				}
			}
			if(isPresent) {
				List<Object> obj = new ArrayList<>();
				obj.add(articleses);
				response.setBody(obj);
				response.setCode(2);
				response.setDescription("L'ARTICLE QUE VOUS TENTE D'AJOUTER EST DEJA EXISTANTE DANS LA LISTE DES ARTICLES A CREER POUR CE PROJET, DANS CETTE RUBRIQUE. PRIERE DE BIEN VOULOIR LA SUPPRIMER D'ABORD AVANT DE L'AJOUTER.");
			}else {
				List<ArticleSessionDTO> articless = (List<ArticleSessionDTO>) articleses;
				articless.add(art);
				request.getSession().setAttribute("ARTICLE_SES", articless);
				response.setCode(1);
				List<Object> obj = new ArrayList<>();
				obj.add(request.getSession().getAttribute("ARTICLE_SES"));
				response.setDescription("SUCCESS");
				response.setBody(obj);
			}
		}
		
		System.out.println(response);
		return response;
	}
	
	
	@PostMapping("/saveExecution")
	@Transactional
	public @ResponseBody Response saveExecution(
			@RequestParam String type,
			@RequestParam String client ,
			@RequestParam String currency,
			@RequestParam String total_amount,
			@RequestParam String subtotal,
			@RequestParam String exec_tax_tot,
			HttpServletRequest request) {
		Response response = new Response();
		
		//Object workExecution
		WorkExecution workexec = new WorkExecution();
		Client cli = this.clientservice.findSingleClient(Long.valueOf(client));
		workexec.setClient(cli);
		workexec.setCreatedDate(new Date());
		workexec.setCurrency(currency);
		workexec.setLastUpdated(new Date());
		workexec.setNature(type);
		workexec.setSubTotal(Double.valueOf(subtotal.replace(" ", "")));
		workexec.setTotalAmount(Double.valueOf(total_amount.replace(" ", "")));
		workexec.setTotalVat(Double.valueOf(exec_tax_tot.replace(" ", "")));
		workexec.setWhoCreated(this.utilitieservice.getUsername());
		workexec = this.workexecutifservice.save(workexec);
		
		
		//Formulation rubrique
		Set<WorkRubric> rubriques = new HashSet<WorkRubric>();
		
		List<ArticleSessionDTO> articleses = (List<ArticleSessionDTO>) request.getSession().getAttribute("ARTICLE_SES");
		for(ArticleSessionDTO art : articleses) {
			WorkRubric wr = new WorkRubric();
			wr.setName(art.getRubliquename());
			wr.setNum(Long.valueOf(art.getRubliqueid()));
			wr.setWorks(workexec);
			rubriques.add(wr);
		}
		List<WorkRubric> rs = new ArrayList<>(rubriques);
		List<WorkRubric> newRs = new ArrayList<>();
		for(WorkRubric r : rs) {
			r = this.workrubricsserviceimpl.save(r);
		}
		
		for(ArticleSessionDTO art : articleses) {
			RubricItem it = new RubricItem();
			it.setArt_total(art.getArt_total());
			it.setArt_total_nvat(art.getArt_total_nvat());
			it.setArticle(art.getArticle());
			it.setQuantity(art.getQuantity());
			it.setRubliqueid(art.getRubliqueid());
			it.setRubliquename(art.getRubliquename());
			it.setTax(art.getTax());
			it.setTax_amount(art.getTax_amount());
			it.setUnity(art.getUnity());
			it.setUnity_price(art.getUnity_price());
			for(WorkRubric r : rs) {
				if(art.getRubliquename().equals(r.getName()) && art.getRubliqueid().equals(r.getNum().toString())) {
					it.setRubric(r);
				}
			}
			
			it = this.rubricitemservice.save(it);
			
		}
		response.setCode(1);
		request.getSession().setAttribute("ARTICLE_SES",null);
		request.getSession().setAttribute("RUBRIQUE_SES",null);
		
		request.getSession().setAttribute("CURR_SES",null);
		request.getSession().setAttribute("TAX_SES",null);
		request.getSession().setAttribute("CLI_SES",null);
		request.getSession().setAttribute("TYP_SES",null);
		//System.out.println(workexec);
		return response;
	}
	
//	@GetMapping("/ordersExec")
//	public String fetch(Model model) {
//		return "works/ordersExec";
//	}
	
	@GetMapping("/ordersExec")
	public String index(Model model) {
		
		return this.index(1, model);
		
	}

	@GetMapping("/ordersExec/{pageno}")
	public String index(@PathVariable() int pageno, Model model) {
		int pagesize = 50; //Integer.valueOf(this.environment.getProperty("config.pagination.number"));
		
		Page<WorkExecution> works = this.workexecutifservice.findAllPage(pageno, pagesize);
		model.addAttribute("works", works.getContent());
		//model.addAttribute("organisations", this.organizationservice.fetch());
		//model.addAttribute("users", listusers);
		model.addAttribute("jsName", "ordersExec.js"); //super.js
		model.addAttribute("currentPage", pageno);
		model.addAttribute("totalpages",works.getTotalPages());
		model.addAttribute("totalItems",works.getTotalElements());
		model.addAttribute("size", works.getContent().size());
		//model.addAttribute("search",new SearchHospitalisationDTO());
		return "works/ordersExec";
	}
	@PostMapping ("/searchProject")
	public @ResponseBody Response fetchProject(@RequestParam String client_id) {
		Response response = new Response();
		Client client = this.clientservice.findSingleClient(Long.valueOf(client_id));
		List<WorkExecution> works = this.workexecutifservice.fetch(client);
		for(WorkExecution w : works) {
			w.setClient(null);
			w.setRubrics(null);
			//w.set
		}
		response.setCode(1);
		response.setBody(new ArrayList<>(works));
		return response;
	}
	
	@PostMapping("/searchRubrics")
	public @ResponseBody Response fetchRubric(@RequestParam String projectid) {
		Response response = new Response();
		WorkExecution work = this.workexecutifservice.fetch(Long.valueOf(projectid));
		List<WorkRubric> rubrics = this.workrubricsserviceimpl.fetch(work);
		for(WorkRubric r: rubrics) {
			r.setItems(null);
			r.setWorks(null);
		}
		response.setCode(1);
		response.setBody(new ArrayList<>(rubrics));
		return response;
	}
	
	@PostMapping("/searchRubricsItems")
	public @ResponseBody Response fetchItems(@RequestParam String rubricid) {
		Response response = new Response();
		WorkRubric rubric = this.workrubricsserviceimpl.fetch(Long.valueOf(rubricid));
		response.setCode(1);
		for(RubricItem i : rubric.getItems()) {
			i.setArticles(null);
			i.setRubric(null);
		}
		response.setBody(new ArrayList<>(rubric.getItems()));
		return response;
	}
	
	@PostMapping("/getArticleDetails")
	public @ResponseBody Response fetchArticle(@RequestParam String articleid) {
		Response response = new Response();
		RubricItem article = this.rubricitemservice.fetch(Long.valueOf(articleid));
		double takQty = 0.0;
		if(article.getArticles().size()>0) {
			for(ArticleInvoice a : article.getArticles()) {
				takQty = takQty + a.getQuantity();
			}
		}
		ItemDTO item = new ItemDTO();
		
		item.setTotquantity(article.getQuantity());
		item.setTakquantity(takQty);
		item.setRemquantity(item.getTotquantity()-takQty);
		item.setUnity(article.getUnity());
		item.setUnityprice(article.getUnity_price());
		item.setTaxRate(article.getTax());
		item.setArticleName(article.getArticle());
		item.setTotalTax(article.getTax_amount());
		item.setRubricName(article.getRubliquename());
		item.setArticleName(article.getArticle());
		//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> :"+item.getRubricName());
		
		double takrate = item.getTakquantity()*100/item.getTotquantity();
		double remrate = 100 - takrate;
		item.setRemrate(remrate);
		item.setTakrate(takrate);
		
		List<ItemDTO> lst = new ArrayList<>();
		lst.add(item);
		response.setCode(1);
		response.setBody(new ArrayList<>(lst));
		return response;
	}
	
	
	@PostMapping("/managerExecInvoiceSession")
	public @ResponseBody Response manageExecutionSession(
			@RequestParam String quantity,
			@RequestParam String taxrate,
			@RequestParam String taxamount,
			@RequestParam String subtotat,
			@RequestParam String grandtotal,
			@RequestParam String progressRate,
			@RequestParam String unity,
			@RequestParam String unityPrice,
			@RequestParam String articleid,
			@RequestParam String taxmount,
			@RequestParam String rubricname,
			@RequestParam String articlename,
			@RequestParam String previousProgressRate,
			HttpServletRequest request
			) {
		Response response = new Response();
		ItemSessionDTO item = new ItemSessionDTO();
		item.setArticleid(articleid);
		item.setArticlename(articlename);
		item.setCurrentRate(Double.valueOf(progressRate));
		item.setPreviousRate(Double.valueOf(previousProgressRate));
		item.setQuantity(Double.valueOf(quantity));
		item.setRubricname(rubricname);
		//item.setArticlename(articlename);
		item.setTotalvatinc(Double.valueOf(grandtotal));
		item.setTotalwvat(Double.valueOf(subtotat));
		item.setUnity(unity);
		item.setUnityPrice(Double.valueOf(unityPrice));
		item.setVatamount(Double.valueOf(taxamount));
		item.setVatRate(Double.valueOf(taxrate));
		System.out.println("Item : "+item);
		if(request.getSession().getAttribute("EX_IT_SES")==null) {
			List<ItemSessionDTO> items = new ArrayList<>();
			items.add(item);
			request.getSession().setAttribute("EX_IT_SES", items);
			response.setCode(1);
			response.setDescription("Success");
			List<Object> obj = new ArrayList<>();
			obj.add(items);
			response.setBody(obj);
		}else {
			List<ItemSessionDTO> cartse = (List<ItemSessionDTO>) request.getSession().getAttribute("EX_IT_SES");
			boolean isPresent = false;
			for(ItemSessionDTO a : cartse) {
				if(a.getArticleid().trim().equals(articleid)) {
					isPresent=true;
				}
			}
			if(isPresent) {
				List<Object> obj = new ArrayList<>();
				for(ItemSessionDTO arti : cartse) {
					obj.add(arti);
				}
				response.setBody(obj);
				response.setCode(2);
				response.setDescription("L'ARTICLE EST DEJA EXISTANT DANS LA LISTE DES ARTICLES A FACTURER. PRIERE DE BIEN VOULOIR LE SUPPRIMER DANS LA LISTE D'ABORD AVANT DE L'AJOUTER.");
				
			}else {
				List<ItemSessionDTO> arts = (List<ItemSessionDTO>) cartse;
				arts.add(item);
				request.getSession().setAttribute("EX_IT_SES", arts);
				cartse = (List<ItemSessionDTO>) request.getSession().getAttribute("EX_IT_SES");
				response.setCode(1);
				List<Object> obj = new ArrayList<>();
				obj.add(arts);
				response.setDescription("SUCCESS");
				response.setBody(obj);
			}
		}
		System.out.println(response);
		return response;
	}
	
	@PostMapping("/saveExecInvoice")
	@Transactional
	public @ResponseBody Response saveExecutionInvoice(
			@RequestParam String client_id,
			@RequestParam String contrat_id,
			@RequestParam String subTotal,
			@RequestParam String totalVat,
			@RequestParam String grandtotal,
			@RequestParam String pyt_mod,
			@RequestParam String ristourneamount,
			@RequestParam String ristournepourcentage,
			HttpServletRequest request) {
		
		
		Response response = new Response();
		
		Client client = this.clientservice.findSingleClient(Long.valueOf(client_id));
		double total = Double.valueOf(grandtotal.replace(" ", ""));
		double vat = Double.valueOf(totalVat.replace(" ", ""));
		double subTotale = Double.valueOf(subTotal.replace(" ", ""));
		//recuperer la session des items
		List<ItemSessionDTO> items =  (List<ItemSessionDTO>) request.getSession().getAttribute("EX_IT_SES");
		//System.out.println("test............");
		if(items.size()>0) {
			Invoice invoice = new Invoice();
			String regBy = this.utilitieservice.getConnectedUserInfo().getFirstName()+" "+this.utilitieservice.getConnectedUserInfo().getLastName();
			Organisation organisation = this.utilitieservice.getOrganisationFromConnectedUser();
			invoice.setClient(client);
			//invoice.setType(2);
			invoice.setCreatedBy(regBy);
			invoice.setCreatedOn(new Date());
			invoice.setDelivaryAddress(client.getAddInfos());
			invoice.setInvoiceDate(new Date());
			invoice.setOrder(null);
			invoice.setOrganisation(organisation);
			invoice.setPaymentMode(pyt_mod);
			//invoice.setReferenceNumber(regBy);
			invoice.setPrintFormat("2");
			invoice.setStatus(1);
			invoice.setType(5);
			invoice.setOrganisation(organisation);
			invoice.setReferenceNumber(this.getInvoiceReference(this.utilitieservice.getOrganisationFromConnectedUser().getId()));
			invoice.setTotalAmount(total);
			invoice.setVatAmount(vat);
			invoice.setSubtotal(subTotale);
			
			if(!ristournepourcentage.isEmpty()) {
				invoice.setRistourneAmount(Double.valueOf(ristourneamount.replace(" ", "")));
				invoice.setRistourneRate(Double.valueOf(ristournepourcentage.replace(" ", "")));
			}else {
				invoice.setRistourneAmount(0.0);
				invoice.setRistourneRate(0.0);
			}
			
			

			invoice = this.invoiceserviceimpl.save(invoice);
			List<ArticleInvoice> articles = new ArrayList<>();
			for(ItemSessionDTO art : items) {
				
				ArticleInvoice artInv = new ArticleInvoice();
				RubricItem rubricItem = this.rubricitemservice.fetch(Long.valueOf(art.getArticleid()));
				artInv.setDescription(art.getArticlename());
				artInv.setInvoice(invoice);
				
				artInv.setProgressRate(art.getCurrentRate());
				artInv.setQuantity(art.getQuantity());
				artInv.setRefOnOrder(null);
				artInv.setTax(art.getVatamount());
				artInv.setTaxMiseasure("");
				artInv.setTaxRate(art.getVatRate());
				artInv.setTitle(art.getArticlename());
				artInv.setTotal(art.getTotalvatinc());
				artInv.setTotalNVat(art.getTotalwvat());
				artInv.setUnity(art.getUnity());
				artInv.setUnityPrice(art.getUnityPrice());
				artInv.setRubricitem(rubricItem);	
				
				articles.add(artInv);
			}
			this.articleserviceimpl.saveArticleInvoice(articles);
		}
		//RubricItem item = null;
		response.setCode(1);
		response.setDescription("");
		request.getSession().setAttribute("EX_IT_SES", null);
		return response;
	}

	private String getInvoiceReference(Long org_id) {
		//1. Voir si le numero exist
		int year = Calendar.getInstance().get(Calendar.YEAR);
		String pre = "0000";
		List<InvoiceNumbering> num = this.invoicenumberingservice.find(year, this.utilitieservice.getOrganisationFromConnectedUser().getId());
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
		String de = "";
		if(String.valueOf(ref).length()<4) {
			de = pre.substring(String.valueOf(ref).length())+String.valueOf(ref)+"/"+year;
		}else {
			de=String.valueOf(ref)+"/"+year;
		}
		return de;
	}
	
	@PostMapping(value={"/rubriqueModification","/ordersExec/rubriqueModification"})
	public @ResponseBody Response modifyRubrique(@RequestParam String id, @RequestParam String name) {
		Response response = new Response();
		WorkRubric rubrique = this.workrubricsserviceimpl.fetch(Long.valueOf(id));
		rubrique.setName(name);
		this.workrubricsserviceimpl.save(rubrique);
		response.setCode(1);
		return response;
	}
	
	@PostMapping(value= {"/deleteRubrique","ordersExec/deleteRubrique"})
	public @ResponseBody Response deleteRubrique(@RequestParam String id) {
		Response response = new Response();
		WorkRubric rubrique = this.workrubricsserviceimpl.fetch(Long.valueOf(id));
		boolean checkIfUsed = false;
		List<RubricItem> items = rubrique.getItems();
		if(items.size()>0) {
			for(RubricItem it : items) {
				List<ArticleInvoice> art = it.getArticles();
				if(art.size()>0) {
					checkIfUsed = true;
					break;
				}
			}
		}
		if(checkIfUsed) {
			response.setCode(2);
			response.setDescription("Impossible de supprimer la rubrique car certains articles de celui-ci ont déjà fait objet d'une facturation.");
			
		}else {
			this.workrubricsserviceimpl.remove(rubrique);
			response.setCode(1);
		}
		
		return response;
	}
	
	@PostMapping(value= {"/addNewRubrique","/ordersExec/addNewRubrique"})
	public @ResponseBody Response addNewRubrique(@RequestParam String id, @RequestParam String name) {
		Response response = new Response();
		WorkExecution work = this.workexecutifservice.fetch(Long.valueOf(id));
		WorkRubric rubrique = new WorkRubric();
		rubrique.setName(name);
		rubrique.setWorks(work);
		this.workrubricsserviceimpl.save(rubrique);
		response.setCode(1);
		return response;
	}
	
	
	@PostMapping(value = {"/addNewArticle","/ordersExec/addNewArticle"})
	public @ResponseBody Response addNewArticle(
			@RequestParam String total, 
			@RequestParam String totalnvat,
			@RequestParam String taxamnt,
			@RequestParam String taxrate,
			@RequestParam String unityprice,
			@RequestParam String quantity,
			@RequestParam String unity, 
			@RequestParam String article, 
			@RequestParam String rubid ) {
		Response response = new Response();
		
		WorkRubric rubrique = this.workrubricsserviceimpl.fetch(Long.valueOf(rubid));
		RubricItem item = new RubricItem();
		item.setArt_total(Double.valueOf(total));
		item.setArt_total_nvat(Double.valueOf(totalnvat));
		item.setArticle(article);
		item.setQuantity(Double.valueOf(quantity));
		item.setRubliqueid(rubid);
		item.setRubliquename(rubrique.getName());
		item.setRubric(rubrique);
		item.setTax(Double.valueOf(taxrate));
		item.setTax_amount(Double.valueOf(taxamnt));
		item.setUnity(unity);
		item.setUnity_price(Double.valueOf(unityprice));
		
		this.rubricitemservice.save(item);
		response.setCode(1);		
		
		
		return response;
	}
	
	@PostMapping(value= {"/upArticle","/ordersExec/upArticle"})
	public @ResponseBody Response updateitem(
			@RequestParam String total, 
			@RequestParam String totalnvat,
			@RequestParam String taxamnt,
			@RequestParam String taxrate,
			@RequestParam String unityprice,
			@RequestParam String quantity,
			@RequestParam String unity, 
			@RequestParam String article, 
			@RequestParam String articleid 
			) {
		Response response = new Response();
		RubricItem item = this.rubricitemservice.fetch(Long.valueOf(articleid));
		WorkRubric rubrique = item.getRubric();
		item.setArt_total(Double.valueOf(total));
		item.setArt_total_nvat(Double.valueOf(totalnvat));
		item.setArticle(article);
		item.setQuantity(Double.valueOf(quantity));
		item.setRubliquename(rubrique.getName());
		item.setTax(Double.valueOf(taxrate));
		item.setTax_amount(Double.valueOf(taxamnt));
		item.setUnity(unity);
		item.setUnity_price(Double.valueOf(unityprice));
		this.rubricitemservice.save(item);
		
		response.setCode(1);		
		
		return response;
	}
	
	@PostMapping(value= {"/deleteItemOnWO","/ordersExec/deleteItemOnWO"})
	public @ResponseBody Response deleteItem(@RequestParam String id) {
		Response response = new Response();
		
		RubricItem item = this.rubricitemservice.fetch(Long.valueOf(id));
		if(item.getArticles().size()>0) {
			response.setCode(2);
			response.setDescription("Impossible de supprimer cet article car il a déjà fait object d'une facturation.");
		}else {
			this.rubricitemservice.delete(item);
			response.setCode(1);
		}
		return response;
	}
}
