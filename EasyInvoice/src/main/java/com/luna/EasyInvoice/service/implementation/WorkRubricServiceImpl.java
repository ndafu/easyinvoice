package com.luna.EasyInvoice.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.luna.EasyInvoice.entities.RubricItem;
import com.luna.EasyInvoice.entities.WorkExecution;
import com.luna.EasyInvoice.entities.WorkRubric;
import com.luna.EasyInvoice.repository.RubricItemRepository;
import com.luna.EasyInvoice.repository.WorkRubricRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class WorkRubricServiceImpl {
	
	final private WorkRubricRepository workRubricrepo;
	final private RubricItemRepository rubricrepo;

	public WorkRubric save(WorkRubric rub) {
		if(rub!=null) {
			return this.workRubricrepo.save(rub);
		}else {
			return null;
		}
	}
	
	public List<WorkRubric> fetch(WorkExecution works){
		return this.workRubricrepo.findByWorks(works);
	}
	
	public WorkRubric fetch(Long id) {
		return this.workRubricrepo.findById(id).get();
	}

	public void remove(WorkRubric rubrique) {
		List<RubricItem> items = rubrique.getItems();
		this.rubricrepo.deleteAllInBatch(items);
		this.workRubricrepo.delete(rubrique);
	}
}
