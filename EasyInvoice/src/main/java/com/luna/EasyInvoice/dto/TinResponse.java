package com.luna.EasyInvoice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TinResponse {
	@JsonProperty("success")
	private boolean success;
	 
	 @JsonProperty("msg")
	 private String message;
	
	 @JsonProperty("result")
	 private TinResponsePojo result;
}
