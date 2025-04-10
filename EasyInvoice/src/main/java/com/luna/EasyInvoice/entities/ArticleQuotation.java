package com.luna.EasyInvoice.entities;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;

@Data
@Entity
@DiscriminatorValue("1")
public class ArticleQuotation extends Article{
	private double totalNVAT;
	private double totalVAT;
	private double total;
	
	@ManyToOne
	@JoinColumn(name = "quotation_id", nullable = true)
	private Quotation quotation;
	
	@OneToMany(mappedBy = "articleQuotation")
	private List<ArticleOrder> articleOrder;
}
