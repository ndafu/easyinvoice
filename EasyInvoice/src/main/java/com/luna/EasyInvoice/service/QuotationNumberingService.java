package com.luna.EasyInvoice.service;

import java.util.List;

import com.luna.EasyInvoice.entities.QuotationNumbering;

public interface QuotationNumberingService {
	List<QuotationNumbering> find(int year, Long org);
	QuotationNumbering saveQuotation (QuotationNumbering numbering);
}
