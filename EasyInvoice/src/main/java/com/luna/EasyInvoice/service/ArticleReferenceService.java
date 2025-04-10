package com.luna.EasyInvoice.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.luna.EasyInvoice.entities.ArticleReference;

public interface ArticleReferenceService {
	ArticleReference save(ArticleReference article);
	List<ArticleReference> findArticle(boolean status);
	List<ArticleReference> findArticle();
	ArticleReference findArticle(Long id);
	Page<ArticleReference> getAllArticleRefPaged(int pageno, int pagesize); 
	
}
