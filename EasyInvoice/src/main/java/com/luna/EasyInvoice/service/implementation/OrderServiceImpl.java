package com.luna.EasyInvoice.service.implementation;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.luna.EasyInvoice.entities.Client;
import com.luna.EasyInvoice.entities.Invoice;
import com.luna.EasyInvoice.entities.Order;
import com.luna.EasyInvoice.entities.Organisation;
import com.luna.EasyInvoice.pojo.StatisticPojo;
import com.luna.EasyInvoice.repository.OrderRepository;
import com.luna.EasyInvoice.service.OrderService;
import com.luna.EasyInvoice.utility.UtilitiesService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	UtilitiesService utilitiesservice;
	
	@Override
	public Order save(Order order) {
		if(order!=null) {
			return this.orderRepository.save(order);
		}
		else return null;
	}

	@Override
	public List<Order> fetch() {
		
		return this.orderRepository.findAll(Sort.by("id").descending());
	}
	
	public Page<Order> fetch(int pageno, int pagesize) {
		Pageable pageable = PageRequest.of(pageno -1,pagesize, Sort.by("id").descending());
		if(this.utilitiesservice.checkIfHasRole("ROLE_ADMIN")) {
			return this.orderRepository.findAll(pageable);
		}else {
			return this.orderRepository.findByOrganisation(this.utilitiesservice.getOrganisationFromConnectedUser(),pageable);
		}
	}


	@Override
	public Order fetch(Long id) {
		Optional<Order> orders = this.orderRepository.findById(id);
		if(orders.isPresent()) return orders.get();
		else return null;
	}

	@Override
	public List<Order> fetch(Client client) {
		return this.orderRepository.findByClientOrderByIdDesc(client);
	}

	@Override
	public List<Order> fetch(String reference) {
		
		return this.orderRepository.findByRefOrderByIdDesc(reference);
	}
	
	public List<StatisticPojo> countOrder(){
		return this.orderRepository.countOrder();
	}
	
	public List<StatisticPojo> countOrder(Organisation organisation){
		return this.orderRepository.countOrder(organisation);
	}
	
	

}
