package com.luna.EasyInvoice.componement;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class SetUpClient implements  ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		
	}
	
//	@Autowired
//	ClientServiceImpl clientservice;
// 
//    boolean alreadySetup = false;
//
//	@Override
//	public void onApplicationEvent(ContextRefreshedEvent event) {
//		if(!isClientCreate("00000000")) {
//			Client client = new Client();
//			client.setName("N/A");
//			client.setPhone("00000000");
//			clientservice.saveClient(client);
//		}
//	}
//	
//	@Transactional
//    boolean isClientCreate(String phonenumber) {
//        List<Client> clients = clientservice.findClientWithPhone(phonenumber);
//        if (clients.size()<=0 ) {
//        	System.out.println("Test creation 1");
//        	return false;
//        }else {
//        	System.out.println("Test creation 2");
//        	return true;
//        }
//    }
}
