package com.luna.EasyInvoice.utility;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class Response {
	private int code;
	private String description;
	private List<Object> body;
	
	
}
