package com.luna.EasyInvoice.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.luna.EasyInvoice.entities.Quotation;

public interface QuotationService {
	Quotation save(Quotation quotation);
	Page<Quotation> fetch(int pageno, int pagesize);
	Quotation fetch(Long id);
	List<Quotation> fetch(int status);
}
