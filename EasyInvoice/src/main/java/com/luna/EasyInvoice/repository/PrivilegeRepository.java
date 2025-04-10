package com.luna.EasyInvoice.repository;


import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.luna.EasyInvoice.entities.Privilege;

public interface PrivilegeRepository extends JpaRepositoryImplementation<Privilege, Integer> {
	Privilege findByName(String priv);
}
