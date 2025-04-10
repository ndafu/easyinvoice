package com.luna.EasyInvoice.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
public class Tax {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String title;
	private String abbreviation;
	private double rate;
	private String comment;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date registrationDate;
	
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date lastUpdate;
	
	private boolean status;
	
	@OneToMany(mappedBy = "tax")
	@JsonIgnore
	private List<ArticleReference> articleReferences;

	@Override
	public String toString() {
		return "Tax [id=" + id + ", title=" + title + ", abbreviation=" + abbreviation + ", rate=" + rate + ", comment="
				+ comment + ", registrationDate=" + registrationDate + ", lastUpdate=" + lastUpdate + ", status="
				+ status + "]";
	}
	
}
