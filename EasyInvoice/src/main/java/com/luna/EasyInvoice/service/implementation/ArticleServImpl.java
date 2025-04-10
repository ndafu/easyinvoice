package com.luna.EasyInvoice.service.implementation;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.luna.EasyInvoice.entities.Article;
import com.luna.EasyInvoice.entities.ArticleInvoice;
import com.luna.EasyInvoice.entities.Quotation;
import com.luna.EasyInvoice.repository.ArticleRepository;
import com.luna.EasyInvoice.service.ArticleService;

@Service
public class ArticleServImpl implements ArticleService {

	@Autowired
	ArticleRepository articlerepo;
	
	@Override
	public Article saveArticle(Article article) {
		if(article != null) {
			return this.articlerepo.save(article);
		}
		else{
			return null;
		}
	}
	
	@Override
	public List<Article> saveArticle(List<Article> articles){
		if(articles.size()>0) {
			return this.articlerepo.saveAll(articles);
		}else {
			return null;
		}
	}
	
	@Override
	public ArticleInvoice fetch(Long id){
		ArticleInvoice article = new ArticleInvoice();
		Optional<Article> articles = this.articlerepo.findById(id);
		if(articles.isPresent()) {
			article = (ArticleInvoice) articles.get();
		}else {
			article = null;
		}
		return article;
	}
	
	@Override
	public void delete(ArticleInvoice article){
		if(article!=null) {
			this.articlerepo.delete(article);
		}
	}
	
	public List<ArticleInvoice> saveArticleInvoice(List<ArticleInvoice> articles){
		if(articles.size()>0) {
			return this.articlerepo.saveAll(articles);
		}else {
			return null;
		}
	}
	

	@Override
	public List<Article> fetchArticles() {
		
		return this.articlerepo.findAll(Sort.by("id").descending());
	}

	@Override
	public Page<Article> fetchArticles(int pageno, int pagesize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Article findArticlebyid(Long id) {
		Optional<Article> articles = this.articlerepo.findById(id);
		if(articles.isPresent()) {
			return articles.get();
		}
		else {
			return null;
		}
	}

	@Override
	public List<Article> fetchArticles(Quotation quotation) {
		// TODO Auto-generated method stub
		return null;
	}

}
