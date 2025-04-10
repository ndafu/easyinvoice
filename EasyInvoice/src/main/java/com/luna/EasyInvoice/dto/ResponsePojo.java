package com.luna.EasyInvoice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({"success","msg","token"})
public class ResponsePojo {
	 
	@JsonProperty("success")
	private boolean success;
	 
	 @JsonProperty("msg")
	 private String message;
	
	 @JsonProperty("result")
	 private ResultPojo result;
}

