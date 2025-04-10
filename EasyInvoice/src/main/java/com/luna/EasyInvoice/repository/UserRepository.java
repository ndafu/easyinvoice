package com.luna.EasyInvoice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.luna.EasyInvoice.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
	
	@Query("SELECT b FROM User b WHERE b.organisation = ?#{principal.organisation}") 
	Page<User> findByOrganisation(Pageable pageable);
}
