package com.luna.EasyInvoice.dto;

import lombok.Data;

@Data
public class ItemDTO {
	private double remquantity;
	private double takquantity;
	private double totquantity;
	private String unity;
	private double unityprice;
	private double takrate;
	private double remrate;
	private double taxRate;
	private double totalTax;
	private String rubricName;
	private String articleName;
}
