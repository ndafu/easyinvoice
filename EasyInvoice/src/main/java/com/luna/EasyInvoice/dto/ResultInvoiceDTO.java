package com.luna.EasyInvoice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({"invoice_number","invoice_registered_number","invoice_registered_date"})
public class ResultInvoiceDTO {
	@JsonProperty("invoice_number")
	private String invoiceNumber;
	@JsonProperty("invoice_registered_date")
	private String registrationdate;
	@JsonProperty("invoice_registered_number")
	private String registrationNumber;
}
