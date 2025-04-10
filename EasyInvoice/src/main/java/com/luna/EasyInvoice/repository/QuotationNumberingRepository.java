package com.luna.EasyInvoice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.luna.EasyInvoice.entities.QuotationNumbering;

public interface QuotationNumberingRepository extends JpaRepositoryImplementation<QuotationNumbering, Long> {
	List<QuotationNumbering> findByYearAndOrgIdOrderByIdDesc(int year, Long org);
}
