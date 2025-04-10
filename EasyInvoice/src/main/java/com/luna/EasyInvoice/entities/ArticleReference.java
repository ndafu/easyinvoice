package com.luna.EasyInvoice.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = { "title","org_id","tax_id"}) })
public class ArticleReference {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String title;
	private String description;
	private double unityPrice;
	private boolean status;
	private String unity;
	
	//@Column(columnDefinition = "DATE DEFAULT CURRENT_DATE")
	private Date registrationDate;
	
	@ManyToOne
	@JoinColumn(name = "tax_id", nullable = true)
	private Tax tax;
	
	@ManyToOne
	@JoinColumn(name = "org_id", nullable = true)
	@JsonIgnore
	private Organisation organisation;

	@Override
	public String toString() {
		return "ArticleReference [id=" + id + ", title=" + title + ", description=" + description + ", unityPrice="
				+ unityPrice + ", status=" + status + "]";
	}
	
}
