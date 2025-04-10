package com.luna.EasyInvoice.dto;



import lombok.Data;

@Data
public class DebtRenderDTO {
	private Long id;
	private String name;
	private double debtAmount;
	private double paidAmount;
	private double ration;
}