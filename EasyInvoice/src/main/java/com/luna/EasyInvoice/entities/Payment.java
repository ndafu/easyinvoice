package com.luna.EasyInvoice.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

import lombok.Data;

@Entity
@Data
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private double amount;
	private double totalvat;
	private double totalpaid;
	private Date paymentDate;
	@Column
	@Size(max = 150)
	private String paymentReference;
	
	private int paidMode; //1. Bank , 2. Cash.
	@Column
	@Size(max = 60)
	private String bankName;
	@Column
	@Size(max = 45)
	private String bankAccount;
	@Lob
	private String comments;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "debt_id", nullable = false)
	private DebtRecovery debt;
}
