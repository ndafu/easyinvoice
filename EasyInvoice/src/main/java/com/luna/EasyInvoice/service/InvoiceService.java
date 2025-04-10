package com.luna.EasyInvoice.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.luna.EasyInvoice.entities.ArticleOrder;
import com.luna.EasyInvoice.entities.Invoice;
import com.luna.EasyInvoice.entities.Order;

public interface InvoiceService {
	Invoice save(Invoice invoice);
	Page<Invoice> fetch(int pageno, int pagesize);
	List<Invoice> fetch(Order order);
	List<Invoice> fetch(int status);
	Invoice fetch(Long id);
	
	List<ArticleOrder> getArticleStatus(Order order);
}
