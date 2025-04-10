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

import lombok.Data;

@Data
@Entity
public class WorkRubric {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name;
	private Long num;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "work_id", nullable = false)
	private WorkExecution works;
	
	@OneToMany(mappedBy = "rubric")
	private List<RubricItem> items;
}
