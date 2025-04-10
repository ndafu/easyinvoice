package com.luna.EasyInvoice.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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

import com.luna.EasyInvoice.entities.ArticleOrder;
import com.luna.EasyInvoice.entities.ArticleQuotation;
import com.luna.EasyInvoice.entities.Invoice;
import com.luna.EasyInvoice.entities.Order;
import com.luna.EasyInvoice.entities.Quotation;
import com.luna.EasyInvoice.pojo.ArticleQPojo;
import com.luna.EasyInvoice.pojo.OrderPojo;
import com.luna.EasyInvoice.pojo.OrganisationPojo;
import com.luna.EasyInvoice.service.implementation.ArticleServImpl;
import com.luna.EasyInvoice.service.implementation.OrderServiceImpl;
import com.luna.EasyInvoice.service.implementation.OrganisationServImpl;
import com.luna.EasyInvoice.service.implementation.QuotationServiceImpl;
import com.luna.EasyInvoice.service.implementation.TaxServImpl;
import com.luna.EasyInvoice.service.implementation.UserPrincipal;
import com.luna.EasyInvoice.utility.Response;
import com.luna.EasyInvoice.utility.UtilitiesService;

@Controller
public class OrderController {

	@Autowired
	QuotationServiceImpl quotationServiceImpl;

	@Autowired
	TaxServImpl taxServiceImpl;

	@Autowired
	OrderServiceImpl orderserviceimpl;

	@Autowired
	OrganisationServImpl organisationServiceImpl;

	@Autowired
	UtilitiesService utilitiesService;

	@Autowired
	ArticleServImpl articleServiceImpl;
	
	@Autowired
	Environment environment;

	@GetMapping("/orders")
	public String index(Model model) {
		return this.index(1, model);
	}

	@GetMapping("/orders/{pageno}")
	public String index(@PathVariable() int pageno, Model model) {
		int pagesize = Integer.valueOf(this.environment.getProperty("config.pagesize"));
		
		Page<Order> orders = this.orderserviceimpl.fetch(pageno,pagesize); //int pageno, int pagesize
		model.addAttribute("organisation", this.utilitiesService.getOrganisationFromConnectedUser());
		model.addAttribute("orders", orders.getContent());
		model.addAttribute("currentPage", pageno);
		//model.addAttribute(quotations);
//		model.addAttribute("organisation", this.utilitiesService.getOrganisationFromConnectedUser());
		model.addAttribute("totalpages",orders.getTotalPages());
		model.addAttribute("size", pagesize);
		model.addAttribute("jsName", "super.js"); //super.js
		model.addAttribute("organisation", this.organisationServiceImpl.findOrganisation().get(0));
		
		return "invoice/orders";
	}

	@PreAuthorize("hasAnyRole('COM_MANAGER','COM_HEAD_FINA','COM_CASH')")
	@GetMapping("/order")
	public String register(Model model) {
		// System.out.println("quotation ....... : "+.get(0).getGrandTotal());
		model.addAttribute("taxs", this.taxServiceImpl.getActiveTaxList());
		model.addAttribute("quotations", this.quotationServiceImpl.fetch(1));
		model.addAttribute("jsName", "super.js"); //super.js
		return "invoice/order";
	}

	@PreAuthorize("hasAnyRole('COM_MANAGER','COM_HEAD_FINA','COM_CASH')")
	@PostMapping("/saveO")
	@Transactional
	public @ResponseBody Response save(@RequestParam String quotation, @RequestParam String order_date,
			@RequestParam String order_ref) {
		Response response = new Response();
		Quotation quot = this.quotationServiceImpl.fetch(Long.valueOf(quotation));
		UserPrincipal princ = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String createdBy = princ.getUserName();
		String org = princ.getOrganisation();
		Order order = new Order();

		order.setCreatedBy(createdBy);
		order.setCreatedOn(new Date());
		order.setOrganisation(this.utilitiesService.getOrganisationFromConnectedUser());

		order.setClient(quot.getClient());
		order.setComments(quot.getComments());
		order.setDelivaryAddress(quot.getDelivaryAddress());
		order.setInvoiceAddress(quot.getInvoiceAddress());
		order.setOrderDate(this.utilitiesService.getdateFromString(order_date));
		order.setQuotation(quot);

		order.setSubTotal(quot.getSubTotal());
		order.setTotalAmount(quot.getGrandTotal());
		order.setVatAmount(quot.getTotalTax());
		order.setRef(order_ref);
		order = this.orderserviceimpl.save(order);
		for (ArticleQuotation art : quot.getArticlesQuotation()) {
			ArticleOrder ar = new ArticleOrder();
			ar.setDescription(art.getDescription());
			ar.setQuantity(art.getQuantity());
			ar.setTitle(art.getTitle());
			ar.setTotal(art.getTotal());
			ar.setUnityPrice(art.getUnityPrice());
			ar.setTotalNVAT(art.getTotalNVAT());
			ar.setTotalVAT(art.getTotalVAT());
			ar.setOrder(order);
			ar.setArticleQuotation(art);
			ar.setTaxRate(art.getTaxRate());
			ar.setTaxMiseasure("%");
			articleServiceImpl.saveArticle(ar);
		}
		quot.setStatus(2);
		this.quotationServiceImpl.save(quot);
		response.setCode(1);
		response.setDescription("SUCCESS");
		return response;
	}

	@PostMapping("/viewO")
	public @ResponseBody Response view(@RequestParam String id) {
		Response response = new Response();
		Order order = this.orderserviceimpl.fetch(Long.valueOf(id));
		if (order != null) {
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
			orderp.setCurrency(order.getQuotation().getCurrency());
			orderp.setTotalTax(order.getVatAmount());
			orderp.setIsTaxable(order.getQuotation().getTaxApplicable());
			List<ArticleQPojo> articles = new ArrayList<>();
			for (ArticleOrder art : order.getArticles()) {
				System.out.println("Article : " + art.getTaxRate());
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
			if (order.getOrganisation() != null) {
				OrganisationPojo org = new OrganisationPojo();
				org.setAddressAvenue(order.getOrganisation().getAddressAvenue());
				org.setAddressCommune(order.getOrganisation().getAddressCommune());
				org.setAddressNumber(order.getOrganisation().getAddressNumber());
				org.setAddressProvince(order.getOrganisation().getAddressProvince());
				org.setAddressQuartier(order.getOrganisation().getAddressQuartier());
				org.setAdress(order.getOrganisation().getAdress());
				org.setLogo(order.getOrganisation().getLogo());
				org.setName(order.getOrganisation().getName());
				org.setTelephone(order.getOrganisation().getTelephone());
				org.setActivitySecter(order.getOrganisation().getActivitySecter());
				org.setFiscalCenter(order.getOrganisation().getFiscalCenter());
				org.setJuridictionForm(order.getOrganisation().getJuridictionForm());
				org.setPostalNumber(order.getOrganisation().getPostalNumber());
				org.setTin(order.getOrganisation().getTin());
				org.setTradeNumber(order.getOrganisation().getTradeNumber());
				org.setVATPayer(order.getOrganisation().isVATPayer());
				orderp.setOrganisation(org);
			}
			orderp.setArticles(articles);
			response.setCode(1);
			response.setDescription("SUCCESS");
			List<Object> list = new ArrayList<>();
			list.add(orderp);
			response.setBody(list);
		}
		return response;
	}
}
