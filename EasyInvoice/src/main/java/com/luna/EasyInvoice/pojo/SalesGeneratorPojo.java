package com.luna.EasyInvoice.pojo;

import java.io.Serializable;

import lombok.Data;

@Data
public class SalesGeneratorPojo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7366237344540039866L;

	private Long id;
	private String title;
	private String designation;
	private double quantity;
	private double unity_price;
	private double tax;
	private double totalNVAT;
	private double totalIncVAT;
	private double totalVAT;
}
