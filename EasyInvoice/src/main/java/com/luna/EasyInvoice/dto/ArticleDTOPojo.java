package com.luna.EasyInvoice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({"item_designation","item_quantity","item_price","item_ct","item_tl","item_price_nvat","vat","item_price_wvat","item_total_amount"})
public class ArticleDTOPojo {
	@JsonProperty("item_designation")
	private String designation;
	@JsonProperty("item_quantity")
	private double quantity;
	@JsonProperty("item_price")
	private double price;
	@JsonProperty("item_ct")
	private double consTax;
	@JsonProperty("item_tl")
	private double levyTax;
	@JsonProperty("item_price_nvat")
	private double nvatPrice;
	@JsonProperty("vat")
	private double vat;
	@JsonProperty("item_price_wvat")
	private double wvatPrice;
	@JsonProperty("item_total_amount")
	private double totalAmount;
	
	private String unity;
}
