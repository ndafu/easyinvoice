package com.luna.EasyInvoice.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.luna.EasyInvoice.entities.Tax;
import com.luna.EasyInvoice.service.implementation.TaxServImpl;
import com.luna.EasyInvoice.utility.Response;


@Controller
public class TaxController {

	@Autowired
	TaxServImpl taxservice;
	
	@Autowired
	Environment env;
	
	@GetMapping("/taxs")
	public String index(Model model) {
		return this.index(1,model);
	}
	
	@GetMapping("/taxs/{pageno}")
	public String index(@PathVariable() int pageno, Model model) {
		int pagesize = Integer.valueOf(env.getProperty("config.pagesize")) ;
		System.out.println("Page size : "+this.env.getProperty("config.pagesize"));
		
		Page<Tax> ctypage = this.taxservice.getAllTaxesPaged(pageno, pagesize);
		model.addAttribute("taxes", ctypage.getContent());
		model.addAttribute("currentPage", pageno);
		model.addAttribute("totalpages",ctypage.getTotalPages());
		model.addAttribute("size", pagesize);
		model.addAttribute("jsName", "super.js"); //super.js
		return "administration/tax";
	}
	
	@PostMapping("/saveTax")
	public @ResponseBody Response save(
				@RequestParam String tax_comment,
				@RequestParam String tax_rate,
				@RequestParam String tax_abbr,
				@RequestParam String tax_status,
				@RequestParam String tax_title
			) {
		Response response = new Response();
		Tax tax = new Tax();
		
		tax.setAbbreviation(tax_abbr);
		tax.setComment(tax_comment);
		tax.setLastUpdate(new Date());
		tax.setRate(Double.valueOf(tax_rate));
		tax.setRegistrationDate(new Date());
		if(tax_status.equalsIgnoreCase("true")) {
			tax.setStatus(true);
		}else {
			tax.setStatus(false);
		}
		tax.setTitle(tax_title);
		this.taxservice.save(tax);
		
		response.setCode(1);
		response.setDescription("SUCCESS");
		return response;
				
	}
	
	@PostMapping("searchTax")
	public @ResponseBody Response search(@RequestParam String id) {
		Response response = new Response();
		Tax tax = this.taxservice.getTaxList(Long.valueOf(id));
		response.setCode(1);
		response.setDescription("SUCCESS");
		List<Object> obj = new ArrayList<>();
		obj.add(tax);
		response.setBody(obj);
		return response;
	}
}
