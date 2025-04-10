package com.luna.EasyInvoice.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class ArticleSessionDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5529974957086249609L;
	private String rubliqueid;
	private String rubliquename;
	private String article;
	private String unity;
	private double quantity;
	private double unity_price;
	private double tax;
	private double tax_amount;
	private double art_total_nvat;
	private double art_total;
}
