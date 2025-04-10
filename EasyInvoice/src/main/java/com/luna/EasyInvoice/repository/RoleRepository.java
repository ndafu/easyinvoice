package com.luna.EasyInvoice.repository;


import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.luna.EasyInvoice.entities.Role;

public interface RoleRepository extends JpaRepositoryImplementation<Role, Integer> {
	Role findByName(String role);
}
