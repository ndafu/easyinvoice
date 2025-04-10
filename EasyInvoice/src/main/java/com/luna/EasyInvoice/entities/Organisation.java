package com.luna.EasyInvoice.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = { "tin"}),@UniqueConstraint(columnNames = { "tradeNumber"}) })
public class Organisation {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name;
	private String tin;
	private String fiscalCenter;
	private String activitySecter;
	private String juridictionForm;
	private String adress;
	private boolean isVATPayer;
	private String accountTitle;
	private String bankAccount;
	private String bankName;
	private String representativeName;
	private String representativePosition;
	private boolean isCTPayer; //Consumption tax, Taxe de consommation
	private boolean isLVPayer; //Levy tax, prelevement forfaitaire
	
	private String addressAvenue;
	private String addressCommune;
	private String addressNumber;
	private String addressProvince;
	private String addressQuartier;
	private String telephone;
	private String postalNumber;
	private String tradeNumber;
	
	@Lob
	private String logo;
	
	@OneToMany(mappedBy = "organisation")
	@JsonIgnore
	private List<Client> clients;
	
	@OneToMany(mappedBy = "organisation")
	@JsonIgnore
	private List<ArticleReference> articlereference;
	
	@OneToMany(mappedBy = "organisation")
	@JsonIgnore
	private List<Quotation> quotations;
	
	@OneToMany(mappedBy = "organisation")
	@JsonIgnore
	private List<Order> orders;
	
	@OneToMany(mappedBy = "organisation")
	@JsonIgnore
	private List<Invoice> invoices;
	
	@OneToMany(mappedBy = "organisation", orphanRemoval = true)
	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Interconnection> interconnections;

	@Override
	public String toString() {
		return "Organisation [id=" + id + ", name=" + name + ", tin=" + tin + ", fiscalCenter=" + fiscalCenter
				+ ", activitySecter=" + activitySecter + ", juridictionForm=" + juridictionForm + ", adress=" + adress
				+ ", isVATPayer=" + isVATPayer + ", accountTitle=" + accountTitle + ", bankAccount=" + bankAccount
				+ ", bankName=" + bankName + ", representativeName=" + representativeName + ", representativePosition="
				+ representativePosition + ", isCTPayer=" + isCTPayer + ", isLVPayer=" + isLVPayer + ", addressAvenue="
				+ addressAvenue + ", addressCommune=" + addressCommune + ", addressNumber=" + addressNumber
				+ ", addressProvince=" + addressProvince + ", addressQuartier=" + addressQuartier + ", telephone="
				+ telephone + ", postalNumber=" + postalNumber + ", tradeNumber=" + tradeNumber + ", logo=" + logo
				+ "]";
	}
	
	
	
}
