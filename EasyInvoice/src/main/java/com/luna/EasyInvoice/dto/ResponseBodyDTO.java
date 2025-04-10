package com.luna.EasyInvoice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({"success","msg","electronic_signature","result"})
public class ResponseBodyDTO {
	@JsonProperty("success")
	private boolean success;
	@JsonProperty("msg")
	private String message;
	@JsonProperty("electronic_signature")
	private String ebmsACK;
	@JsonProperty("result")
	private ResultInvoiceDTO result;
}
