package com.luna.EasyInvoice.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({ "username", "password"})
public class ConnexionPojo {
	private String username;
	private String password;
}
