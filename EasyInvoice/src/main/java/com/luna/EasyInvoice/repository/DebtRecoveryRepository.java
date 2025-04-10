package com.luna.EasyInvoice.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.luna.EasyInvoice.entities.DebtRecovery;
import com.luna.EasyInvoice.entities.Organisation;


public interface DebtRecoveryRepository extends JpaRepository<DebtRecovery, Long> {

	List<DebtRecovery> findByStatusNotIn(List<Integer> status, Sort descending);

	
	List<DebtRecovery> findByStatusNotInAndNextAppointmentLessThanEqualOrderByNextAppointmentDesc(List<Integer> status,Date todatDate);

	
}
