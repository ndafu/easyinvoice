package com.luna.EasyInvoice.service.implementation;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.luna.EasyInvoice.entities.Quotation;
import com.luna.EasyInvoice.repository.QuotationRepository;
import com.luna.EasyInvoice.service.QuotationService;
import com.luna.EasyInvoice.utility.UtilitiesService;

@Service
public class QuotationServiceImpl implements QuotationService {

	@Autowired
	QuotationRepository quotationrepo;
	
	@Autowired 
	UtilitiesService utilitiesservice;
	
	@Override
	public Quotation save(Quotation quotation) {
		if(quotation !=null) {
			return this.quotationrepo.save(quotation);
		}
		else{
			return null;
		}
	}

	@Override
	public Page<Quotation> fetch(int pageno, int pagesize) {
		Pageable pageable = PageRequest.of(pageno -1,pagesize, Sort.by("id").descending());
		UserPrincipal princ = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();  
		System.out.println(princ.getAuthorities());
		if (princ != null && princ.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
			return this.quotationrepo.findAll(pageable);
		}else {
			return this.quotationrepo.findByOrganisation(this.utilitiesservice.getOrganisationFromConnectedUser(), pageable);
		}
	}

	@Override
	//@PostFilter("hasRole('ADMIN') or filterObject.organisation == authentication.principal.organisation") 
	public Quotation fetch(Long id) {
		Optional<Quotation> quotation = this.quotationrepo.findById(id);
		if(quotation.isPresent()) return quotation.get();
		else return null;
	}

	@Override
	@PostFilter("hasAnyRole('COM_MANAGER')") 
	public List<Quotation> fetch(int status) {
		return this.quotationrepo.findByStatusOrderByIdDesc(this.utilitiesservice.getOrganisationFromConnectedUser(),status);
	}

}
