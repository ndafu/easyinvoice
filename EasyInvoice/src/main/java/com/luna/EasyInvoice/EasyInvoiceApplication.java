package com.luna.EasyInvoice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@ComponentScan(basePackages = "com.luna.EasyInvoice")
public class EasyInvoiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EasyInvoiceApplication.class, args);
	}

}
