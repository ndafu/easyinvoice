package com.luna.EasyInvoice.service.implementation;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.luna.EasyInvoice.entities.ArticleReference;
import com.luna.EasyInvoice.repository.ArticleReferenceRepository;
import com.luna.EasyInvoice.service.ArticleReferenceService;
import com.luna.EasyInvoice.utility.UtilitiesService;

@Service
public class ArticleReferenceServImpl implements ArticleReferenceService {

	@Autowired
	ArticleReferenceRepository articlerepo;
	
	@Autowired
	UtilitiesService utilitiesservice;
	

	
	@Override
	public ArticleReference save(ArticleReference article) {
		if(article!=null) {
			return this.articlerepo.save(article);
		}
		else {
			return null;
		}
	}

	@Override
	public List<ArticleReference> findArticle(boolean status) {
		return this.articlerepo.findByStatus(this.utilitiesservice.getOrganisationFromConnectedUser(),status, Sort.by("id").descending());
	}

	@Override
	public List<ArticleReference> findArticle() {
		
		return this.articlerepo.findAll(Sort.by("id").descending());
	}

	@Override
	public ArticleReference findArticle(Long id) {
		Optional<ArticleReference> art = this.articlerepo.findById(id);
		if(art.isPresent()) {
			return art.get();
		}
		else return null;
	}

	@Override
	public Page<ArticleReference> getAllArticleRefPaged(int pageno, int pagesize) 
	{
		Pageable pageable = PageRequest.of(pageno -1,pagesize, Sort.by("id").descending());
		
		UserPrincipal princ = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();  
		System.out.println(princ.getAuthorities());
		if(this.utilitiesservice.checkIfHasRole("ROLE_ADMIN")) {
			return this.articlerepo.findAll(pageable);
		}else {
			return this.articlerepo.findByOrganisation(this.utilitiesservice.getOrganisationFromConnectedUser(), pageable);
		}
	}
}
