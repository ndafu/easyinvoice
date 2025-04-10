package com.luna.EasyInvoice.repository;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.luna.EasyInvoice.entities.Payment;



public interface PaymentRepository extends JpaRepositoryImplementation<Payment, Long>{

}
