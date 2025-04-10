package com.luna.EasyInvoice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.luna.EasyInvoice.entities.InvoiceNumbering;

public interface InvoiceNumberingRepository extends JpaRepositoryImplementation<InvoiceNumbering, Long>{
	List<InvoiceNumbering> findByYearAndOrgIdOrderByIdDesc(int year, Long org);
}
