package com.luna.EasyInvoice.entities;



import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = { "reference"})})
public class Quotation {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id ;
	@Column(unique = true)
	private String reference;
	private int type;
	private String invoiceAddress;
	private Date quotationDate;
	private String delivaryAddress;
	private String comments;
	private double subTotal;
	private double totalTax;
	private double grandTotal;
	private String taxApplicable; //2, appliquer TVA, 1, pas de tva
	private String currency;
	private int status;
	@ManyToOne
	@JoinColumn(name = "org_id", nullable = false)
	@JsonIgnore
	private Organisation organisation;
	
	@CreatedBy
	private String createdBy;
	@CreatedDate
	private Date createdOn;
	@ManyToOne
	@JoinColumn(name = "client_id", nullable = false)
	private Client client;
	
	@OneToMany(mappedBy = "quotation")	
	@JsonIgnore
	private List<ArticleQuotation> articlesQuotation;
	
	@OneToMany(mappedBy = "quotation")
	@JsonIgnore
	private List<Order> order;

	@Override
	public String toString() {
		return "Quotation [id=" + id + ", reference=" + reference + ", invoiceAddress=" + invoiceAddress
				+ ", quotationDate=" + quotationDate + ", delivaryAddress=" + delivaryAddress + ", comments=" + comments
				+ ", subTotal=" + subTotal + ", totalTax=" + totalTax + ", grandTotal=" + grandTotal
				+ ", taxApplicable=" + taxApplicable + ", currency=" + currency + ", status=" + status
				+ ", organisation=" + organisation + ", createdBy=" + createdBy + ", createdOn=" + createdOn + "]";
	}
	
	
}
