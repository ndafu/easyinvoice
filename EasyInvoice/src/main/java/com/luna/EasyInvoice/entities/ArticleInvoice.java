package com.luna.EasyInvoice.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ColumnDefault;

import lombok.Data;

@Data
@Entity
@DiscriminatorValue("3")
public class ArticleInvoice extends Article{
	private double total;
	private double totalNVat;
	private Long refOnOrder;
	@ColumnDefault(value = "0.0")
	private double progressRate;
	
	
	@ManyToOne
	@JoinColumn(name = "invoice_id", nullable = true)
	private Invoice invoice;
	
	@ManyToOne
	@JoinColumn(name = "rub_it_id", nullable = true)
	private RubricItem rubricitem;
	
	@ManyToOne
	@JoinColumn(name = "article_order_id", nullable = true)
	private ArticleOrder articleOrder;

	@Override
	public String toString() {
		return "ArticleInvoice [total=" + total + ", totalNVat=" + totalNVat
				+ ", refOnOrder=" + refOnOrder + ", getId()=" + getId() + ", getTitle()=" + getTitle()
				+ ", getDescription()=" + getDescription() + ", getUnityPrice()=" + getUnityPrice() + ", getTaxRate()="
				+ getTaxRate() + ", getTaxMiseasure()=" + getTaxMiseasure() + ", getTax()=" + getTax() + ", toString()="
				+ super.toString() + ", getClass()=" + getClass() + "]";
	}
}
