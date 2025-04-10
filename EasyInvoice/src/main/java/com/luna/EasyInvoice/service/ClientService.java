package com.luna.EasyInvoice.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.luna.EasyInvoice.entities.Client;

public interface ClientService {
	Client save(Client client);
	List<Client> findClient();
	Page<Client> getAllClientRefPaged(int pageno, int pagesize); 
	Client findSingleClient(Long id);
	
	Client findClient(String telephone);
	Client findClientByName(String name);
}
