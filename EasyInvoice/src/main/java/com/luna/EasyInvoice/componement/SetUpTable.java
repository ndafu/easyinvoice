package com.luna.EasyInvoice.componement;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;



@Component
public class SetUpTable implements  ApplicationListener<ContextRefreshedEvent> {
	
//	@Autowired
//	TableServiceImpl tableservice;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
//		if(!isTableCreated("N00001")) {
//			TableEntity table = new TableEntity();
//			table.setNumber("N00001");
//			table.setRegistrationDate(new Date());
//			table.setLastModified(new Date());
//			table.setStatus(1);
//			table.setComment("Virtual table/Table virtuelle");
//			tableservice.saveTable(table);
//		}
	}
//	@Transactional
//    boolean isTableCreated(String number) {
//        TableEntity clients = tableservice.findTableWithNumber(number);
//        if (clients==null ) {
//        	return false;
//        }else {
//        	return true;
//        }
//    }

}
