package com.luna.EasyInvoice.service.implementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luna.EasyInvoice.entities.InvoiceNumbering;
import com.luna.EasyInvoice.repository.InvoiceNumberingRepository;
import com.luna.EasyInvoice.service.InvoiceNumberingService;

@Service
public class InvoiceNumberingServiceImpl implements InvoiceNumberingService {

	@Autowired
	InvoiceNumberingRepository invoicerepo;
	
	@Override
	public List<InvoiceNumbering> find(int year, Long org_id) {
		return this.invoicerepo.findByYearAndOrgIdOrderByIdDesc(year,org_id);
	}


	@Override
	public InvoiceNumbering saveInvo(InvoiceNumbering numbering) {
		if(numbering !=null) {
			return this.invoicerepo.save(numbering); 
		}
		else return null;
	}

}
