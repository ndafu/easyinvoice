package com.luna.EasyInvoice.service.implementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luna.EasyInvoice.entities.QuotationNumbering;
import com.luna.EasyInvoice.repository.QuotationNumberingRepository;
import com.luna.EasyInvoice.service.QuotationNumberingService;

@Service
public class QuotationNumberingServiceImpl implements QuotationNumberingService{
	
	@Autowired
	QuotationNumberingRepository quotationnumberingrepo;
	
	@Override
	public List<QuotationNumbering> find(int year, Long org) {
		return this.quotationnumberingrepo.findByYearAndOrgIdOrderByIdDesc(year, org);
	}

	@Override
	public QuotationNumbering saveQuotation(QuotationNumbering numbering) {
		if(numbering!=null) {
			return this.quotationnumberingrepo.save(numbering);
		}else return null;
	}
}
