package com.luna.EasyInvoice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({"success","msg","electronique_signature"})
public class ResponseBodyDTOError {
	@JsonProperty("success")
	private boolean success;
	@JsonProperty("msg")
	private String message;
	@JsonProperty("electronique_signature")
	private String ebmsACK;
}