package com.luna.EasyInvoice.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.luna.EasyInvoice.dto.ArticleDTOPojo;
import com.luna.EasyInvoice.dto.ConnexionPojo;
import com.luna.EasyInvoice.dto.InvoiceDTOPojo;
import com.luna.EasyInvoice.dto.RequestDTO;
import com.luna.EasyInvoice.dto.ResponseBodyDTO;
import com.luna.EasyInvoice.dto.ResponseDTO;
import com.luna.EasyInvoice.dto.ResponsePojo;
import com.luna.EasyInvoice.entities.ArticleInvoice;
import com.luna.EasyInvoice.entities.Interconnection;
import com.luna.EasyInvoice.entities.Invoice;
import com.luna.EasyInvoice.entities.Organisation;
import com.luna.EasyInvoice.service.implementation.InvoiceServiceImpl;
import com.luna.EasyInvoice.service.implementation.OBRSendingServiceImplementation;
import com.luna.EasyInvoice.service.implementation.OrganisationServImpl;
import com.luna.EasyInvoice.utility.JsonUtils;
import com.luna.EasyInvoice.utility.UtilitiesService;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

@Controller
public class SendInvoiceController {
	
	@Autowired
	InvoiceServiceImpl invoiceserviceimpl;
	
	@Autowired
	UtilitiesService utilityservice;
	
	@Autowired
	OrganisationServImpl organisationservice;
	
	@Autowired
	OBRSendingServiceImplementation obrservice;
	
	@Autowired
	Environment environment;
	
	@Scheduled(cron="*/150 * * * * ?")
	public void getInvoice() {
		System.out.println("En cours d'execution ....");
		List<Invoice> invoices = this.invoiceserviceimpl.fetch(2);
		
		
		for(Invoice invoice : invoices) {
			try {
				if(invoice.getOrganisation().getInterconnections().size()>0) {
					this.obrservice.sendInvoice(invoice, invoice.getOrganisation(), invoice.getOrganisation().getInterconnections().get(0).getUrl(), invoice.getOrganisation().getInterconnections().get(0).getUsername(), invoice.getOrganisation().getInterconnections().get(0).getPassword());
				}
				//this.sendInvoice(invoice);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	@Scheduled(cron="*/350 * * * * ?")
	public void getCancelledInvoice() {
		System.out.println("En cours d'execution :envoi des factures annulees ....");
		List<Invoice> invoices = this.invoiceserviceimpl.fetch(0);
		for(Invoice invoice : invoices) {
			try {
				if(invoice.getOrganisation().getInterconnections().size()>0) {
					this.obrservice.cancelInvoice(invoice, invoice.getOrganisation()) ;				
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	

}
