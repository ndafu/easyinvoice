package com.luna.EasyInvoice.service.implementation;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.luna.EasyInvoice.entities.Tax;
import com.luna.EasyInvoice.repository.TaxRepository;
import com.luna.EasyInvoice.service.TaxService;


@Service
public class TaxServImpl implements TaxService {

	@Autowired
	TaxRepository taxrepo;
	
	@Override
	public Tax save(Tax tax) {
		if(tax!=null) {
			return this.taxrepo.save(tax);
		}
		else return null;
	}

	@Override
	public List<Tax> getTaxList() {
		return this.taxrepo.findAll(Sort.by("id").descending());
	}

	@Override
	public List<Tax> getActiveTaxList() {
		return this.taxrepo.findByStatus(true);
	}

	@Override
	public Tax getTaxList(Long id) {
		Optional<Tax> optio = this.taxrepo.findById(id);
		if(optio.isPresent()) {
			return optio.get();
		}
		else return null;
	}

	public Page<Tax> getAllTaxesPaged(int pageno, int pagesize) {
		Pageable pageable = PageRequest.of(pageno -1,pagesize, Sort.by("id").descending());
		return this.taxrepo.findAll(pageable);
	}
}
