package com.luna.EasyInvoice.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.luna.EasyInvoice.entities.InvoiceValidationNumbering;
import com.luna.EasyInvoice.repository.InvoiceValidationNumberingRepository;
import com.luna.EasyInvoice.service.InvoiceValidationNumberingService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class InvoiceValidationNumberingServiceImpl implements InvoiceValidationNumberingService {

	private final InvoiceValidationNumberingRepository invoicerepo;
	@Override
	public List<InvoiceValidationNumbering> find(int year, Long org_id) {
		// TODO Auto-generated method stub
		return this.invoicerepo.findByYearAndOrgIdOrderByIdDesc(year,org_id);
	}

	@Override
	public InvoiceValidationNumbering saveInvo(InvoiceValidationNumbering numbering) {
		if(numbering !=null) {
			return this.invoicerepo.save(numbering); 
		}
		else return null;
	}
}
