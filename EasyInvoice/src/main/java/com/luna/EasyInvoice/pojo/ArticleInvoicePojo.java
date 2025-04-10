package com.luna.EasyInvoice.pojo;

import java.io.Serializable;

import lombok.Data;

@Data
public class ArticleInvoicePojo implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	private String title;
	private String designation;
	private double quantity;
	private double unity_price;
	private double total; 
	private double tax;
	private double taxRate;
	private double totalNVat;
	private String unity;
}
