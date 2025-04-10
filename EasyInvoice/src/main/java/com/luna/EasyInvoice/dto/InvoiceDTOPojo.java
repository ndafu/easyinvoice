package com.luna.EasyInvoice.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({"invoice_number","invoice_date","tp_type","tp_name","tp_TIN","tp_trade_number","tp_postal_number","tp_phone_number","tp_address_province","tp_address_commune","tp_address_quartier","tp_address_avenue","tp_address_number","vat_taxpayer","ct_taxpayer","tl_taxpayer","tp_fiscal_center","tp_activity_sector","tp_legal_form","payment_type","invoice_currency","customer_name","customer_TIN","customer_address","vat_customer_payer","cancelled_invoice_ref","invoice_signature","invoice_identifier","invoice_signature_date","invoice_items"})
public class InvoiceDTOPojo {
	@JsonProperty("invoice_number")
	private String reference;
	@JsonProperty("invoice_date")
	private String invoiceDate;
	@JsonProperty("tp_type")
	private String tpType;
	@JsonProperty("tp_name")
	private String tpName;
	@JsonProperty("tp_TIN")
	private String tpTin;
	@JsonProperty("tp_trade_number")
	private String tpTradeNumber;
	@JsonProperty("tp_postal_number")
	private String tpPostalNumber;
	@JsonProperty("tp_phone_number")
	private String tpPhoneNumber;
	@JsonProperty("tp_address_province")
	private String tpAddressProvince;
	@JsonProperty("tp_address_commune")
	private String tpAddressCommune;
	@JsonProperty("tp_address_quartier")
	private String tpAddressQuartier;
	@JsonProperty("tp_address_avenue")
	private String tpAddressAvenue;
	@JsonProperty("tp_address_number")
	private String tpAddressNumber;
	@JsonProperty("vat_taxpayer")
	private int vatTaxPayer;
	@JsonProperty("ct_taxpayer")
	private int ct_taxpayer;
	@JsonProperty("tl_taxpayer")
	private int tl_taxpayer;
	@JsonProperty("tp_fiscal_center")
	private String tpFiscalCenter;
	@JsonProperty("tp_activity_sector")
	private String tpActivitySector;
	@JsonProperty("tp_legal_form")
	private String tpLegalForm;
	@JsonProperty("payment_type")
	private String paymentType;
	@JsonProperty("invoice_currency")
	private String invoiceCurrency;
	@JsonProperty("customer_name")
	private String customerName;
	@JsonProperty("customer_TIN")
	private String customerTin;
	@JsonProperty("customer_address")
	private String customerAddress;
	@JsonProperty("vat_customer_payer")
	private int customerPayer; 
	@JsonProperty("cancelled_invoice_ref")
	private String cancelledInvoice;
	@JsonProperty("invoice_signature")
	private String signature;
	@JsonProperty("invoice_signature_date")
	private String signatureDate;
	@JsonProperty("invoice_items")
	private List<ArticleDTOPojo> items;
	@JsonProperty("invoice_identifier")
	private String invoiceidentifier;
}
