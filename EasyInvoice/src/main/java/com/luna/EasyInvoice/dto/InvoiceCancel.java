package com.luna.EasyInvoice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({ "invoice_signature"})
public class InvoiceCancel {
	
	@JsonProperty("invoice_signature")
	private String invoiceSignature;
	@JsonProperty("cn_motif")
	private String cancelRaison;
}
