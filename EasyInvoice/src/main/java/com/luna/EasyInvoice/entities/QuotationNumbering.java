package com.luna.EasyInvoice.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;

@Data
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = { "current","year","orgId"})})
public class QuotationNumbering {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private int current;
	private int next;
	private int year;
	private Long orgId;
}
