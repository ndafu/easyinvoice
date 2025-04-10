package com.luna.EasyInvoice.service;


import com.luna.EasyInvoice.dto.InvoiceCancel;
import com.luna.EasyInvoice.dto.InvoiceDTOPojo;
import com.luna.EasyInvoice.dto.ResponseBodyDTO;
import com.luna.EasyInvoice.dto.ResponsePojo;
import com.luna.EasyInvoice.dto.TinResponse;
import com.luna.EasyInvoice.entities.Invoice;
import com.luna.EasyInvoice.entities.Organisation;

public interface OBRSendingService {
	void sendInvoice(Invoice invoice, Organisation organisation, String API_URL, String username, String password) throws Exception ;
	ResponsePojo getConnected(String API_URL, String username, String password) throws Exception;
	ResponseBodyDTO sendInvoice(InvoiceDTOPojo invoice, String API_URL, String token) throws Exception;
	String cancelInvoice(InvoiceCancel invoice, String API_URL, String token) throws Exception;
	TinResponse checkTin(String tin, String API_URL, String username, String password) throws Exception;
}
