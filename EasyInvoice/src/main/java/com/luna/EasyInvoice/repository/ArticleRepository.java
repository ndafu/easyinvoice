package com.luna.EasyInvoice.repository;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.luna.EasyInvoice.entities.Article;

public interface ArticleRepository extends JpaRepositoryImplementation<Article, Long>{
	
}
