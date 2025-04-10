package com.luna.EasyInvoice.pojo;

import java.util.Date;

import lombok.Data;

@Data
public class OrderPojo extends QuotationPojo {
	private String ref;
	private Date orderDate;
	private OrganisationPojo organisation;
}
