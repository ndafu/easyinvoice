package com.luna.EasyInvoice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.luna.EasyInvoice.entities.Client;
import com.luna.EasyInvoice.entities.Order;
import com.luna.EasyInvoice.entities.Organisation;
import com.luna.EasyInvoice.pojo.StatisticPojo;

public interface OrderRepository extends JpaRepositoryImplementation<Order, Long>{
	List<Order> findByClientOrderByIdDesc(Client client);
	List<Order> findByRefOrderByIdDesc(String reference);
	
	@Query("SELECT b FROM ORDER_TBL b WHERE b.organisation = ?1") 
	Page<Order> findByOrganisation(Organisation organisation, Pageable pageable);
	
	@Query("SELECT new com.luna.EasyInvoice.pojo.StatisticPojo(CONCAT(MONTHNAME(S.orderDate),' ',YEAR(S.orderDate)), SUM(S.totalAmount)) FROM ORDER_TBL AS S GROUP BY CONCAT(MONTH(S.orderDate),'',YEAR(S.orderDate)) ORDER BY CONCAT(MONTH(S.orderDate),'',YEAR(S.orderDate)) ASC")
	List<StatisticPojo> countOrder();
	
	@Query("SELECT new com.luna.EasyInvoice.pojo.StatisticPojo(CONCAT(MONTHNAME(S.orderDate),' ',YEAR(S.orderDate)), SUM(S.totalAmount)) FROM ORDER_TBL AS S WHERE S.organisation=?1 GROUP BY CONCAT(MONTH(S.orderDate),'',YEAR(S.orderDate)) ORDER BY CONCAT(MONTH(S.orderDate),'',YEAR(S.orderDate)) ASC")
	List<StatisticPojo> countOrder(Organisation organisation);
}
