package com.luna.EasyInvoice.service;

import java.util.List;

import com.luna.EasyInvoice.entities.Client;
import com.luna.EasyInvoice.entities.Order;

public interface OrderService {
	Order save(Order order);
	List<Order> fetch();
	
	Order fetch(Long id);
	List<Order> fetch(Client client);
	List<Order> fetch(String reference);
}
