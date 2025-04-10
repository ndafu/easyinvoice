package com.luna.EasyInvoice.pojo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.luna.EasyInvoice.entities.Invoice;

import lombok.Data;

@Data
public class InvoicePojo extends Invoice{
	private List<ArticleInvoicePojo> articleInvoicePojo;
	private String cliName;
	private String cliTin;
	private String cliAdress;
	private boolean cliIsVAT;
	private String invoiceRef;
	private String ordRef;
	private Date ordDate;
	private String currency;
	private String VatPayable;
	private OrganisationPojo organisation;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
	private Date invoiceDate;
	private String registrationperson;
	
	private String bankname;
	private String banktitle;
	private String bankaccount;
	private String invoiceType;
	
	private String validationref;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
	private Date validationdateDate;
	
	private String validationPerson;
	private String cancellationreason;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
	private Date cancallationDate;
	private String cancellationPerson;
	
	private double ristourneRate;
	private double ristourneAmount;
	
	private String idEBMSSignature;
}
