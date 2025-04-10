package com.luna.EasyInvoice.service.implementation;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.luna.EasyInvoice.entities.Client;
import com.luna.EasyInvoice.entities.WorkExecution;
import com.luna.EasyInvoice.repository.WorkExecutionRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class WorkExecutionServiceImpl {

	final private WorkExecutionRepository workexecutionrepo;
	
	public WorkExecution save(WorkExecution work) {
		if(work!=null) {
			return this.workexecutionrepo.save(work);
		}else {
			return null;
		}
	}
	
	public Page<WorkExecution> findAllPage(int page, int pagesize) {
		Pageable pageable = PageRequest.of(page -1,pagesize, Sort.by("id").descending());
		return this.workexecutionrepo.findAll(pageable);
	}
	
	public List<WorkExecution> fetch(Client client){
		return this.workexecutionrepo.findByClient(client);
	}
	
	public WorkExecution fetch(Long client){
		return this.workexecutionrepo.findById(client).get();
	}
}
