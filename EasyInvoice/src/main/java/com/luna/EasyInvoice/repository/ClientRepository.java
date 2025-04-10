package com.luna.EasyInvoice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.luna.EasyInvoice.entities.ArticleReference;
import com.luna.EasyInvoice.entities.Client;
import com.luna.EasyInvoice.entities.Organisation;
import com.luna.EasyInvoice.entities.Quotation;

public interface ClientRepository extends JpaRepositoryImplementation<Client, Long>{
	@Query("SELECT b FROM Client b WHERE b.organisation = ?1") //?#{principal.emailAddress}
	Page<Client> findByOrganisation(Organisation org, Pageable pageable);
	
	@Query("SELECT b FROM Client b WHERE b.organisation = ?1") 
	List<Client> findByOrganisation(Organisation org, Sort sort);
	
	@Query("SELECT b FROM Client b WHERE b.organisation = ?1 AND b.telephone LIKE %?2") 
	List<Client> findByTelephoneAndOrganisation(Organisation org, String telephone);

	@Query("SELECT b FROM Client b WHERE b.organisation = ?1 AND b.name LIKE %?2") 
	List<Client> findByNameAndOrganisation(Organisation organisationFromConnectedUser, String name);
}
