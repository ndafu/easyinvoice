package com.luna.EasyInvoice.controller;



import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.luna.EasyInvoice.pojo.StatisticPojo;
import com.luna.EasyInvoice.service.implementation.ArticleServImpl;
import com.luna.EasyInvoice.service.implementation.ClientServImpl;
import com.luna.EasyInvoice.service.implementation.InvoiceServiceImpl;
import com.luna.EasyInvoice.service.implementation.OrderServiceImpl;
import com.luna.EasyInvoice.utility.Response;
import com.luna.EasyInvoice.utility.UtilitiesService;

@Controller
public class Index {
	
	@Autowired
	ArticleServImpl articleservice;
	
	@Autowired
	UtilitiesService utilitieservice;
	
	@Autowired
	ClientServImpl clientservice;
	
	@Autowired
	OrderServiceImpl orderServiceImpl;
	
	@Autowired
	InvoiceServiceImpl invoiceservice;
	
	@RequestMapping(value = "/")
	public String indexe(Model model) {
		return "redirect:/dashboard";
	}
	
	@RequestMapping(value = "/dashboard")
	public String index(Model model) {
		model.addAttribute("articles", this.articleservice.fetchArticles().size());
		model.addAttribute("clients", this.clientservice.findClient().size());
		model.addAttribute("bons",this.orderServiceImpl.fetch().size());
		model.addAttribute("invoices",this.invoiceservice.fetch().size());
		model.addAttribute("jsName", "super.js"); //super.js
		return "dashboard";
	}
	
	@PostMapping("/getDashboardData")
	public @ResponseBody Response getData() {
		Response rsp = new Response();
		//getThe sales
		List<String> key = new ArrayList<>();
		List<String> value = new ArrayList<>();
		List<StatisticPojo> sales = new ArrayList<>();
		if(this.utilitieservice.checkIfHasRole("ROLE_ADMIN")) {
			sales = this.invoiceservice.countInvoice(); 
		}else {
			sales = this.invoiceservice.countInvoice(this.utilitieservice.getOrganisationFromConnectedUser()); 
		}
		
		System.out.println(sales);
		for(StatisticPojo sta : sales) {
			key.add(sta.getKey());
			value.add(sta.getValue()+"");
		}
		key.toArray();
		value.toArray();
		rsp.setCode(1);
		List<Object> obj = new ArrayList<>();
		obj.add(key);
		obj.add(value);
		rsp.setBody(obj);
		return rsp;
	}
	
	@PostMapping("/getDashboardExpenseData")
	public @ResponseBody Response getDataExpense() {
		Response rsp = new Response();
		//getThe sales
		List<String> key = new ArrayList<>();
		List<String> value = new ArrayList<>();
		List<StatisticPojo> expenses = new ArrayList<>();
		if(this.utilitieservice.checkIfHasRole("ROLE_ADMIN")) {
			expenses = this.orderServiceImpl.countOrder();
		}else {
			expenses = this.orderServiceImpl.countOrder(this.utilitieservice.getOrganisationFromConnectedUser()); 
		}
		for(StatisticPojo sta : expenses) {

			key.add(sta.getKey());
			value.add(sta.getValue()+"");
		}
		key.toArray();
		value.toArray();
		
		rsp.setCode(1);
		List<Object> obj = new ArrayList<>();
		obj.add(key);
		obj.add(value);
		rsp.setBody(obj);
		return rsp;
	}
	
}
