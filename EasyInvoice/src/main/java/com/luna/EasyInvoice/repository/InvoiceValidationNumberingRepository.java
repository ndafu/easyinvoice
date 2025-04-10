package com.luna.EasyInvoice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.luna.EasyInvoice.entities.InvoiceValidationNumbering;

public interface InvoiceValidationNumberingRepository extends JpaRepositoryImplementation<InvoiceValidationNumbering, Long>{
	List<InvoiceValidationNumbering> findByYearAndOrgIdOrderByIdDesc(int year, Long org_id);
}
