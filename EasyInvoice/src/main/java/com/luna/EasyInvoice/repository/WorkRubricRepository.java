package com.luna.EasyInvoice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.luna.EasyInvoice.entities.WorkExecution;
import com.luna.EasyInvoice.entities.WorkRubric;

public interface WorkRubricRepository extends JpaRepositoryImplementation<WorkRubric, Long>{

	List<WorkRubric> findByWorks(WorkExecution works);

}
