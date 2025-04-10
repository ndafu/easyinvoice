package com.luna.EasyInvoice.service;

import java.util.List;

import com.luna.EasyInvoice.entities.InvoiceNumbering;

public interface InvoiceNumberingService {
	List<InvoiceNumbering> find(int year, Long org_id);
	InvoiceNumbering saveInvo (InvoiceNumbering numbering);
}
