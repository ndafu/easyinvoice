package com.luna.EasyInvoice.entities;



import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = { "referenceNumber"})})
public class Invoice {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String referenceNumber;
	private Date invoiceDate;
	private int type;
	@Lob
	private String paymentMode;
	//private String 
	private String delivaryAddress;
	private double totalAmount;
	private double vatAmount;
	private double subtotal;
	private int status; //1 : is created, 2 : is validated, 3: sent to OBR, 0 : is rejected or canceled, 4 : paid
	private String cancelRaison;
	@Lob
	private String reponse;
	private String signature;
	private Date signatureDate;
	@ManyToOne
	@JoinColumn(name = "order_id", nullable = true)
	private Order order;
	@ColumnDefault(value = "1")
	private String printFormat;
	@CreatedBy
	private String createdBy;
	@CreatedDate
	private Date createdOn;
	
	@ManyToOne
	@JoinColumn(name = "client_id", nullable = true)
	private Client client;
	
	@ColumnDefault(value = "0")
	private double ristourneRate;
	@ColumnDefault(value = "0")
	private double ristourneAmount;
	
	//ajout
	private String orderReference;
	private Date orderDate;
	private String ebmsMsg;
	private String obrDate;
	private String currency;
	private String validationref;
	@Lob
	private String ebmsACK;
	private Date validatedDate;
	private String validatedPerson;

	@OneToMany(mappedBy = "invoice", orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.FALSE)
	//@OneToMany(mappedBy = "invoice")
	@JsonIgnore
	private List<ArticleInvoice> articles;
	
	@OneToMany(mappedBy = "invoice", orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.FALSE)
	//@OneToMany(mappedBy = "invoice")
	@JsonIgnore
	private List<DebtRecovery> debts;
	
	
	@ManyToOne
	@JoinColumn(name = "org_id", nullable = false)
	private Organisation organisation;
	
	private Date cancelledDate;
	private String cancelledPerson;
	
	@Lob
	private String comment;
}
