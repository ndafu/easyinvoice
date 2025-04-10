package com.luna.EasyInvoice.pojo;

import lombok.Data;

@Data
public class ArticleQPojo {
	private Long id;
	private double quantity;
	private double totalWVAT; 
	private double totalVat;
	private double totalNVAT;
	private double vat;
	private String title;
	private String description;
	private double unityPrice;
}
