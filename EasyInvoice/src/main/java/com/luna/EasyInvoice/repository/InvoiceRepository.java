package com.luna.EasyInvoice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.luna.EasyInvoice.entities.Client;
import com.luna.EasyInvoice.entities.Invoice;
import com.luna.EasyInvoice.entities.Order;
import com.luna.EasyInvoice.entities.Organisation;
import com.luna.EasyInvoice.pojo.StatisticPojo;


public interface InvoiceRepository extends JpaRepositoryImplementation<Invoice, Long>{
	//List<Invoice> findAll(Sort sort);
	@Query("SELECT b FROM Invoice b WHERE b.organisation = ?1") 
	Page<Invoice> findByOrganisation(Organisation organisation, Pageable pageable);
	
	List<Invoice> findByOrder(Order order, Sort sort);
	
	List<Invoice> findByStatus(int status);
	
	@Query("SELECT new com.luna.EasyInvoice.pojo.StatisticPojo(CONCAT(MONTHNAME(S.invoiceDate),' ',YEAR(S.invoiceDate)), SUM(S.totalAmount)) FROM Invoice AS S GROUP BY CONCAT(MONTH(S.invoiceDate),'',YEAR(S.invoiceDate)) ORDER BY CONCAT(MONTH(S.invoiceDate),'',YEAR(S.invoiceDate)) ASC")
	List<StatisticPojo> countInvoice();
	
	@Query("SELECT new com.luna.EasyInvoice.pojo.StatisticPojo(CONCAT(MONTHNAME(S.invoiceDate),' ',YEAR(S.invoiceDate)), SUM(S.totalAmount)) FROM Invoice AS S WHERE S.organisation =?1 GROUP BY CONCAT(MONTH(S.invoiceDate),'',YEAR(S.invoiceDate)) ORDER BY CONCAT(MONTH(S.invoiceDate),'',YEAR(S.invoiceDate)) ASC")
	List<StatisticPojo> countInvoiceByOrganisation(Organisation organisation);

	List<Invoice> findByClientAndStatusInAndValidationrefLikeAndReferenceNumberLikeAndOrganisation(Client client,
			List<Integer> status, String validationref, String regNumber, Organisation org);

	List<Invoice> findByStatusInAndValidationrefLikeAndReferenceNumberLikeAndOrganisation(List<Integer> status,
			String validationref, String regNumber, Organisation org);
	
	//findTop25ByTableOrderByIdDesc
}
