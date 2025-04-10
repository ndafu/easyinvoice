package com.luna.EasyInvoice.entities;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@DiscriminatorValue("2")
public class ArticleOrder extends Article{
	private double discountRate;
	private String discountMiseasure;
	private double discount;
	private double totalNVAT;
	private double totalVAT;
	private double total;
	
	@ManyToOne
	@JoinColumn(name = "order_id", nullable = true)
	@JsonIgnore
	private Order order;
	
	@OneToMany(mappedBy = "articleOrder")
	@JsonIgnore
	private List<ArticleInvoice> articleInvoices;
	
	@ManyToOne
	@JoinColumn(name = "articleQ_id", nullable = true)
	@JsonIgnore
	private ArticleQuotation articleQuotation;

	@Override
	public String toString() {
		return "ArticleOrder [discountRate=" + discountRate + ", discountMiseasure="
				+ discountMiseasure + ", discount=" + discount + ", totalNVAT=" + totalNVAT + ", totalVAT=" + totalVAT
				+ ", total=" + total + ", getId()=" + getId() + ", getTitle()=" + getTitle() + ", getDescription()="
				+ getDescription() + ", getUnityPrice()=" + getUnityPrice() + ", getTaxRate()=" + getTaxRate()
				+ ", getTaxMiseasure()=" + getTaxMiseasure() + ", getTax()=" + getTax() + ", toString()="
				+ super.toString() + ", getClass()=" + getClass() + "]";
	}

	
}
