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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Action {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private int type;
	private Date actionDate;
	@Lob
	private String result;
	@Lob
	private String nextStep;
	@Column
	@Size(max = 45)
	private String createdBy;
	@Column
	@Size(max = 45)
	private String contactedPerson;
	
	@Column
	@Size(max = 12)
	private String contactedTelephone;
	
	@Column
	@Size(max = 45)
	private String contactedEmail;
	@Column
	@Size(max = 45)
	private String whoExecuted;
	@Column
	//@Size(max = 45)
	private int usedMean; // 1.Telephone, 2. Email , 3. Visite, 4. none
	@Lob
	private String comments;
	@Lob
	private String fnalDecision;
	
	private Date nextAppointment;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "debt_id", nullable = false)
	private DebtRecovery debt;
}
