package com.luna.EasyInvoice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.luna.EasyInvoice.entities.Client;
import com.luna.EasyInvoice.entities.WorkExecution;

public interface WorkExecutionRepository extends JpaRepositoryImplementation<WorkExecution, Long>{

	List<WorkExecution> findByClient(Client client);

}
