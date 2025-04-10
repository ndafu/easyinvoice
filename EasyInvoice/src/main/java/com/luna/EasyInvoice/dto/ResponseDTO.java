package com.luna.EasyInvoice.dto;


import lombok.Data;

@Data
public class ResponseDTO {
	private int code;
	private String headers;
	private RequestDTO request;
	private ResponseBodyDTO body;
}
