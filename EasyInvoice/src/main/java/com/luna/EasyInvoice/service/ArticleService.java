package com.luna.EasyInvoice.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.luna.EasyInvoice.entities.Article;
import com.luna.EasyInvoice.entities.ArticleInvoice;
import com.luna.EasyInvoice.entities.ArticleQuotation;
import com.luna.EasyInvoice.entities.Quotation;

public interface ArticleService {
	public Article saveArticle(Article article);
	List<Article> fetchArticles();
	Page<Article> fetchArticles(int pageno, int pagesize);
	Article findArticlebyid(Long id);
	List<Article> fetchArticles(Quotation quotation);
	List<Article> saveArticle(List<Article> articles);
	ArticleInvoice fetch(Long id);
	void delete(ArticleInvoice article);
}
