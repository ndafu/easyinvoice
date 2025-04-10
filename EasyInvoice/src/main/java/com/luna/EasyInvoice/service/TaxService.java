package com.luna.EasyInvoice.service;

import java.util.List;

import com.luna.EasyInvoice.entities.Tax;

public interface TaxService {
	Tax save(Tax tax);
	List<Tax> getTaxList();
	List<Tax> getActiveTaxList();
	Tax getTaxList(Long id);
	
}
