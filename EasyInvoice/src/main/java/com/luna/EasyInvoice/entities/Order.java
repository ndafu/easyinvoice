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

import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.IndexColumn;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity(name="ORDER_TBL")
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private int type;
	private int status;
	private String invoiceAddress;
	private String delivaryAddress;
	private String comments;
	private Date orderDate;
	private double subTotal;
	private double vatAmount;
	private double vatRate;
	private String vatCalculation;
	private double totalAmount;
	@Column(unique = true)
	private String ref;
	
	@ManyToOne
	@JoinColumn(name ="org_id", nullable = false)
	private Organisation organisation;
	
	@CreatedBy
	private String createdBy;
	@CreatedDate
	private Date createdOn;
	
//	@ColumnTransformer(read = "AES_DECRYPT(UNHEX(cardNumber), 'SECRET KEY')", write = "HEX(AES_ENCRYPT(?, 'SECRET KEY'))")
//	private String cardNumber; //field to store the encrypted form of credit card number

	@ManyToOne
	@JoinColumn(name ="quotation_id", nullable = false)
	private Quotation quotation;
	
	@OneToMany(mappedBy = "order")
	@JsonIgnore
	private List<ArticleOrder> articles;
	
	@ManyToOne
	@JoinColumn(name ="client_id", nullable = false)
	private Client client;
	
	@OneToMany(mappedBy = "order")
	@JsonIgnore
	List<Invoice> invoices;

	@Override
	public String toString() {
		return "Order [id=" + id + ", invoiceAddress=" + invoiceAddress + ", delivaryAddress=" + delivaryAddress
				+ ", comments=" + comments + ", orderDate=" + orderDate + ", subTotal=" + subTotal + ", vatAmount="
				+ vatAmount + ", vatRate=" + vatRate + ", vatCalculation=" + vatCalculation + ", totalAmount="
				+ totalAmount + ", ref=" + ref + "]";
	}
	
	
}
