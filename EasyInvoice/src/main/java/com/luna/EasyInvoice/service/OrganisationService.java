package com.luna.EasyInvoice.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.luna.EasyInvoice.entities.Organisation;

public interface OrganisationService {
	Organisation save(Organisation organisation);
	List<Organisation> findOrganisation();
	Page<Organisation> getAllOrganisationRefPaged(int pageno, int pagesize); 
	Organisation findSingleOrganisation(Long id);
}
