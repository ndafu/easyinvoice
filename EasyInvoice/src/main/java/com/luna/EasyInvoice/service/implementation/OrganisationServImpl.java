package com.luna.EasyInvoice.service.implementation;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.luna.EasyInvoice.entities.Organisation;
import com.luna.EasyInvoice.repository.OrganisationRepository;
import com.luna.EasyInvoice.service.OrganisationService;

@Service
public class OrganisationServImpl implements OrganisationService {

	@Autowired
	OrganisationRepository organisationrepo;
	
	@Override
	public Organisation save(Organisation organisation) {
		if(organisation!=null) {
			return this.organisationrepo.save(organisation);
		}
		else return null;
	}

	@Override
	public List<Organisation> findOrganisation() {
		return this.organisationrepo.findAll(Sort.by("id").descending());
	}

	@Override
	public Page<Organisation> getAllOrganisationRefPaged(int pageno, int pagesize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Organisation findSingleOrganisation(Long id) {
		Optional<Organisation> organisations = this.organisationrepo.findById(id);
		if(organisations.isPresent()) return organisations.get();
		else return null;
	}

}
