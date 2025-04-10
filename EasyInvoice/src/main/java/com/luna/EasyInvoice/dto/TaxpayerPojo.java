package com.luna.EasyInvoice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TaxpayerPojo {
	@JsonProperty("tp_name")
	private String name;
}
