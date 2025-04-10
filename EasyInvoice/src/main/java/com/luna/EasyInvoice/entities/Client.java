package com.luna.EasyInvoice.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = { "TIN","org_id"}),@UniqueConstraint(columnNames = { "telephone","org_id"}) })
public class Client {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(unique = true, nullable = false)
	private String name;
	private String contactPerson;

	@Column(unique = false, nullable = true)
	private String TIN;
	
	@Column(nullable = false)
	private String telephone;
	
	private String email;
	
	private String province;
	private String commune;
	private String quartier;
	
	@ColumnDefault("pers")
	private String typeClient;
	
	private String abbr;
	
	@ColumnDefault("false")
	private boolean isEntreprise;

	@Lob
	private String addInfos;
	
	private boolean vatSubject;
	
	//@Column(columnDefinition = "DATE DEFAULT CURRENT_DATE")
	private Date registrationDate;
	
	@OneToMany(mappedBy = "client")
	@JsonIgnore
	private List<Quotation> quotations;
	
	@OneToMany(mappedBy = "client", orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonIgnore
	private List<Invoice> invoices;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "org_id", nullable = false)
	private Organisation organisation;

	@Override
	public String toString() {
		return "Client [id=" + id + ", name=" + name + ", contactPerson=" + contactPerson + ", TIN=" + TIN
				+ ", telephone=" + telephone + ", email=" + email + ", addInfos=" + addInfos + ", vatSubject="
				+ vatSubject + ", registrationDate=" + registrationDate + "]";
	}
	
	
}
