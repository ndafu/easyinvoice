package com.luna.EasyInvoice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.luna.EasyInvoice.entities.Organisation;
import com.luna.EasyInvoice.entities.Quotation;

public interface QuotationRepository extends JpaRepositoryImplementation<Quotation, Long>{
	@Query("SELECT b FROM Quotation b WHERE b.organisation = ?1 AND b.status =?2") 
	List<Quotation> findByStatusOrderByIdDesc(Organisation org, int status);
//	@Query(value = "SELECT * FROM Quotation WHERE LASTNAME = ?1",
//		    countQuery = "SELECT count(*) FROM USERS WHERE LASTNAME = ?1",
//		    nativeQuery = true)
	
	@Query("SELECT b FROM Quotation b WHERE b.organisation = ?1") 
	Page<Quotation> findByOrganisation(Organisation organisation, Pageable pageable);
	
	Optional<Quotation> findById(Long id);
}
