package com.luna.EasyInvoice.dto;

import java.util.Date;


import javax.persistence.Lob;
import javax.validation.constraints.Size;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTAO {

	private Long id;
	private double amount;
	private double totalvat;
	private double totalpaid;
	private Date paymentDate;
	@Size(max = 150)
	private String paymentReference;
	private int paidMode; //1. Bank , 2. Cash.
	@Size(max = 60)
	private String bankName;
	@Size(max = 45)
	private String bankAccount;
	@Lob
	private String comments;
	private Long debt;
}
