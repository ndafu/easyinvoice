package com.luna.EasyInvoice.service.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luna.EasyInvoice.entities.Interconnection;
import com.luna.EasyInvoice.repository.InterconnectionRepository;
import com.luna.EasyInvoice.service.InterconnectionService;

@Service
public class InterconnectionServiceImpl implements InterconnectionService {
	@Autowired
	InterconnectionRepository interconnectionrepo;
	@Override
	public Interconnection save(Interconnection interconnection) {
		if(interconnection!=null) {
			return this.interconnectionrepo.save(interconnection);
		}
		else return null;
	}

	@Override
	public void delete(Interconnection interconnection) {
		if(interconnection!=null) {
			this.interconnectionrepo.delete(interconnection);
		}
	}

}
