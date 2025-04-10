package com.luna.EasyInvoice.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;

@Data
@Entity
public class WorkExecution {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String nature;
	private String currency;
	private double totalAmount;
	private double subTotal;
	private double totalVat;
	private Date createdDate;
	private Date lastUpdated;
	private String whoCreated;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "client_id", nullable = false)
	private Client client;
	
	@OneToMany(mappedBy = "works")
	private List<WorkRubric> rubrics;
	
}
