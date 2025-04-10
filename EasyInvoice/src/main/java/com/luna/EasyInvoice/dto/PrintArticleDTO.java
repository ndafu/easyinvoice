package com.luna.EasyInvoice.dto;

import lombok.Data;

@Data
public class PrintArticleDTO {
	private String title;
	private String quantity;
	private String days;
	private String price;
	private String total;
	private String unity;
	private String unity_price;
	private String totalNVAT;
	private String rubrique;
	private String avancement;
}
