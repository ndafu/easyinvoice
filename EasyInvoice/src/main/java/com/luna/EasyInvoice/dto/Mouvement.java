package com.luna.EasyInvoice.dto;

import java.util.Date;



import lombok.Data;

@Data
public class Mouvement implements Comparable<Mouvement> {
	private String label;
	private double credit;
	private double debit;
	private Date registrationDate;
	@Override
	public int compareTo(Mouvement o) {
		// TODO Auto-generated method stub
		return getRegistrationDate().compareTo(o.getRegistrationDate());
	}
}