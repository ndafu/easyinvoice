package com.luna.EasyInvoice.pojo;

import java.util.Date;
import java.util.List;

import com.luna.EasyInvoice.entities.Client;

import lombok.Data;

@Data
public class QuotationPojo {
	private Long id ;
	private String invoiceAddress;
	private Date quotationDate;
	private String delivaryAddress;
	private String comments;
	private double subTotal;
	private double totalTax;
	private double grandTotal;
	private int status;
	private String currency;
	private String isTaxable;
	private Client client;
	private String reference;
	private List<ArticleQPojo> articles;
	private OrganisationPojo organisation;
}


