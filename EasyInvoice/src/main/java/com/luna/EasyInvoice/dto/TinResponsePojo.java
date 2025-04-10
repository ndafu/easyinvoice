package com.luna.EasyInvoice.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TinResponsePojo {
	@JsonProperty("taxpayer")
	private List<TaxpayerPojo> taxpayer;
}
