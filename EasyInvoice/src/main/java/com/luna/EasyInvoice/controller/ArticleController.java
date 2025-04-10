package com.luna.EasyInvoice.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.luna.EasyInvoice.entities.ArticleReference;
import com.luna.EasyInvoice.mapper.ArticleMapper;
import com.luna.EasyInvoice.service.implementation.ArticleReferenceServImpl;
import com.luna.EasyInvoice.service.implementation.TaxServImpl;
import com.luna.EasyInvoice.utility.UtilitiesService;
import com.poiji.bind.Poiji;



@Controller
public class ArticleController {
	
	@Autowired
	UtilitiesService utilitiesservice;
	
	@Autowired
	TaxServImpl taxservice;
	
	@Autowired
	Environment env;
	
	@Autowired
	ArticleReferenceServImpl articleService;

	@PostMapping("/uploadArticle")
	public String upload(@RequestParam("file") MultipartFile file, RedirectAttributes attributes) {
		//String path = this.env.getProperty("server.upload.folder");
		 final String UPLOAD_DIR = this.env.getProperty("server.upload.folder"); //"tmp";		 
		 if (file.isEmpty()) {
			 return "redirect:/article";
		 }
		 String orName = StringUtils.cleanPath(file.getOriginalFilename());
		 String fileName = orName;
		 Path path = null;
		 try {
			 System.out.println("File 1 : "+fileName);
			 path = Paths.get(UPLOAD_DIR + "/"+fileName);
			 // Path uploadDir = Paths.get(dirName);
			 String uploadPath = path.toFile().getAbsolutePath();
			 System.out.println("Absolute path : "+uploadPath);
			 Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			 //List<ArticleMapper> articles = ;
			 for(ArticleMapper art : this.generateList(uploadPath)) {
				 ArticleReference article = new ArticleReference();
				 article.setDescription(art.getDescription());
				 article.setOrganisation(this.utilitiesservice.getOrganisationFromConnectedUser());
				 article.setRegistrationDate(new Date());
				 article.setStatus(true);
				 article.setTax(this.taxservice.getTaxList(Long.valueOf(art.getTax())));
				 article.setTitle(art.getTitle());
				 article.setUnityPrice(Double.valueOf(art.getPrice()));
				 this.articleService.save(article);
			 }
			 //
		 } catch (IOException e) {
			 e.printStackTrace();
		 }
		 //
		 
		 return "redirect:/article";
	}
	
	@GetMapping("/uploadArticle")
	public String upload() {
		return "redirect:/article";
	}
	private List<ArticleMapper> generateList(String path) {
		File file = new File(path);
		List<ArticleMapper> invoices = Poiji.fromExcel(file, ArticleMapper.class);
		return invoices;
	}
}


