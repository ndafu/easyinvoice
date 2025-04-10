package com.luna.EasyInvoice.service;

import java.util.List;

import com.luna.EasyInvoice.entities.InvoiceValidationNumbering;


public interface InvoiceValidationNumberingService {
	List<InvoiceValidationNumbering> find(int year, Long org_id);
	InvoiceValidationNumbering saveInvo (InvoiceValidationNumbering numbering);
}
