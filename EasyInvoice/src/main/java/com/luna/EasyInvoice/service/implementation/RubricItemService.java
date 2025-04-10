package com.luna.EasyInvoice.service.implementation;

import org.springframework.stereotype.Service;

import com.luna.EasyInvoice.entities.RubricItem;
import com.luna.EasyInvoice.repository.RubricItemRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RubricItemService {

	final private RubricItemRepository rubricitemrepo;
	
	public RubricItem save(RubricItem rub) {
		if(rub!=null) {
			return this.rubricitemrepo.save(rub);
		}else {
			return null;
		}
	}
	
	public RubricItem fetch(Long id) {
		return this.rubricitemrepo.findById(id).get();
	}

	public void delete(RubricItem item) {
		this.rubricitemrepo.delete(item);
	}
}
