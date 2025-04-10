package com.luna.EasyInvoice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.luna.EasyInvoice.entities.ArticleReference;
import com.luna.EasyInvoice.entities.Organisation;

public interface ArticleReferenceRepository extends JpaRepositoryImplementation<ArticleReference, Long>{
	
	@Query("SELECT b FROM ArticleReference b WHERE b.organisation = ?1 AND b.status = ?2")
	List<ArticleReference> findByStatus(Organisation org, boolean status, Sort sort);
	
	@Query("SELECT b FROM ArticleReference b WHERE b.organisation = ?1") 
	Page<ArticleReference> findByOrganisation(Organisation org, Pageable pageable);
}
