package com.luna.EasyInvoice.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class RubriqueDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8889019595306916302L;
	private Long num;
	private String name;
}
