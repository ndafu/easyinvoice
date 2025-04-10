package com.luna.EasyInvoice.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
public class RubricItem {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String rubliqueid;
	private String rubliquename;
	private String article;
	private String unity;
	private double quantity;
	private double unity_price;
	private double tax;
	private double tax_amount;
	private double art_total_nvat;
	private double art_total;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "rub_id", nullable = false)
	private WorkRubric rubric;
	
	@OneToMany(mappedBy = "rubricitem", orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.FALSE)
	//@OneToMany(mappedBy = "invoice")
	@JsonIgnore
	private List<ArticleInvoice> articles;
}
