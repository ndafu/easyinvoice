package com.luna.EasyInvoice.service;

import com.luna.EasyInvoice.entities.Interconnection;

public interface InterconnectionService {
	Interconnection save(Interconnection interconnection);
	void delete(Interconnection interconnection);
}
