package com.luna.EasyInvoice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.luna.EasyInvoice.entities.Tax;

public interface TaxRepository extends JpaRepositoryImplementation<Tax, Long>{
	List<Tax> findByStatus(boolean status);
}
