package com.luna.EasyInvoice.service.implementation;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.luna.EasyInvoice.entities.Client;
import com.luna.EasyInvoice.repository.ClientRepository;
import com.luna.EasyInvoice.service.ClientService;
import com.luna.EasyInvoice.utility.UtilitiesService;

@Service
public class ClientServImpl implements ClientService {

	@Autowired
	ClientRepository clientrepo;
	
	@Autowired
	UtilitiesService utilitiesservice;
	
	@Override
	public Client save(Client client) {
		if(client!=null) {
			return this.clientrepo.save(client);
		}else{
			return null;
		}
	}

	@Override
	public List<Client> findClient() {
		return this.clientrepo.findByOrganisation(this.utilitiesservice.getOrganisationFromConnectedUser(),Sort.by("id").descending());
	}

	@Override
	public Page<Client> getAllClientRefPaged(int pageno, int pagesize) {
		
		Pageable pageable = PageRequest.of(pageno -1,pagesize, Sort.by("id").descending());
		
		UserPrincipal princ = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();  
		System.out.println(princ.getAuthorities());
		if (princ != null && princ.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
			return this.clientrepo.findAll(pageable);
		}else {
			return this.clientrepo.findByOrganisation(this.utilitiesservice.getOrganisationFromConnectedUser(), pageable);
		}		
	}

	@Override
	public Client findSingleClient(Long id) {
		Optional<Client> clients = this.clientrepo.findById(id);
		if(clients.isPresent()) return clients.get();
		else return null;
	}

	@Override
	public Client findClient(String telephone) {
		List<Client> clients = this.clientrepo.findByTelephoneAndOrganisation(this.utilitiesservice.getOrganisationFromConnectedUser(), telephone);
		if(clients.size()>0) {
			return clients.get(0);
		}else {
			return null;
		}
	}
	
	@Override
	public Client findClientByName(String name) {
		List<Client> clients = this.clientrepo.findByNameAndOrganisation(this.utilitiesservice.getOrganisationFromConnectedUser(), name);
		if(clients.size()>0) {
			return clients.get(0);
		}else {
			return null;
		}
	}
}
