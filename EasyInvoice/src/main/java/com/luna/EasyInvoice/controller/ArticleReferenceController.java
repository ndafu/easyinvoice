package com.luna.EasyInvoice.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.luna.EasyInvoice.entities.ArticleReference;
import com.luna.EasyInvoice.entities.Tax;
import com.luna.EasyInvoice.service.implementation.ArticleReferenceServImpl;
import com.luna.EasyInvoice.service.implementation.TaxServImpl;
import com.luna.EasyInvoice.utility.Response;
import com.luna.EasyInvoice.utility.UtilitiesService;

@Controller
public class ArticleReferenceController {
	
	@Autowired
	TaxServImpl taxservice;
	
	@Autowired
	UtilitiesService utilitiesservice;
	
	@Autowired
	ArticleReferenceServImpl articleReferenceService;
	
	@Autowired
	Environment env;
	
	@GetMapping("/article")
	public String index(Model model) {
		return this.index(1, model);
	}
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/article/{pageno}")
	public String index(@PathVariable() int pageno, Model model) {
		int pagesize = Integer.valueOf(this.env.getProperty("config.pagesize"));
		Page<ArticleReference> articles = this.articleReferenceService.getAllArticleRefPaged(pageno, pagesize);
		model.addAttribute("taxs", this.taxservice.getActiveTaxList());
		model.addAttribute("articles", articles.getContent());
		model.addAttribute("currentPage", pageno);
		model.addAttribute("totalpages",articles.getTotalPages());
		model.addAttribute("size", pagesize);
		model.addAttribute("jsName", "super.js"); //super.js
		return "administration/article";
	}
	@PreAuthorize("isAuthenticated() and hasAnyRole('COM_MANAGER','COM_HEAD_ADMIN','COM_HEAD_FINA','COM_LOG_OFF')")
	@PostMapping("/saveArticle")
	public @ResponseBody Response save(
				@RequestParam String art_title, 
				@RequestParam String art_description, 
				@RequestParam String art_unity_price, 
				@RequestParam String art_tax, 
				@RequestParam String art_status, 
				@RequestParam String art_unity
			) {
		Response response = new Response();
		Tax tax = this.taxservice.getTaxList(Long.valueOf(art_tax));
		ArticleReference article = new ArticleReference();
		article.setDescription(art_description);
		article.setRegistrationDate(new Date());
		if(art_status.equalsIgnoreCase("true")) {
			article.setStatus(true);
		}else {
			article.setStatus(false);
		}
		article.setOrganisation(this.utilitiesservice.getOrganisationFromConnectedUser());
		article.setTax(tax);
		article.setTitle(art_title);
		article.setUnity(art_unity);
		article.setUnityPrice(Double.valueOf(art_unity_price));
		
		this.articleReferenceService.save(article);
		response.setCode(1);
		response.setDescription("SUCCESS");
		return response;
	}
	
	@PostMapping("/findArticle")
	public @ResponseBody Response search(
				@RequestParam String id
			)
	{
		Response response = new Response();
		ArticleReference article = this.articleReferenceService.findArticle(Long.valueOf(id));
		response.setCode(1);
		response.setDescription("SUCCESS");
		List<Object> obj = new ArrayList<>();
		obj.add(article);
		response.setBody(obj);
		return response;
	}
	
	@PostMapping("/updateArticle")
	public @ResponseBody Response update(
				@RequestParam String id,
				@RequestParam String title,
				@RequestParam String unityPrice,
				@RequestParam String description,
				@RequestParam String tax,
				@RequestParam String status,
				@RequestParam String unity
			) {
		Response response = new Response();
		ArticleReference article = this.articleReferenceService.findArticle(Long.valueOf(id));
		article.setDescription(description);
		if(status.equalsIgnoreCase("false")) {
			article.setStatus(false);
		}else {
			article.setStatus(true);
		}
		article.setUnity(unity);
		article.setTax(this.taxservice.getTaxList(Long.valueOf(tax)));
		article.setTitle(title);
		article.setUnityPrice(Double.valueOf(unityPrice));
		
		this.articleReferenceService.save(article);
		response.setCode(1);
		response.setDescription("SUCCESS");
		
		return response;
	}
}
