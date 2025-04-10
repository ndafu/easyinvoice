package com.luna.EasyInvoice.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class ItemSessionDTO  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5523190245999349932L;
	private String rubricname;
	private String articleid;
	private String articlename;
	private double quantity;
	private double currentRate;
	private double previousRate;
	private double unityPrice;
	private String unity;
	private double vatRate;
	private double vatamount;
	private double totalwvat;
	private double totalvatinc;
}
