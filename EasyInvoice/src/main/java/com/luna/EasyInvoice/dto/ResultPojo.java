package com.luna.EasyInvoice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({"token"})
public class ResultPojo {
	@JsonProperty("token")
	private String token;
}
