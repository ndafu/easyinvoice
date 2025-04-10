package com.luna.EasyInvoice.dto;

import java.util.Date;


import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActionDTO {
	private Long id;
	private int type;
	private Date actionDate;
	private String result;
	private String nextStep;
	@Size(max = 45)
	private String createdBy;
	@Size(max = 45)
	private String contactedPerson;
	@Size(max = 12)
	private String contactedTelephone;
	@Size(max = 45)
	private String contactedEmail;
	@Size(max = 45)
	private String whoExecuted;
	private int usedMean; // 1.Telephone, 2. Email , 3. Visite, 4. none
	private String comments;
	private String fnalDecision;
	private Date nextAppointment;
	private Long debt;
}
