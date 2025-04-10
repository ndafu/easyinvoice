package com.luna.EasyInvoice.service.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;

import com.luna.EasyInvoice.entities.ArticleInvoice;
import com.luna.EasyInvoice.entities.ArticleOrder;
import com.luna.EasyInvoice.entities.Client;
import com.luna.EasyInvoice.entities.Invoice;
import com.luna.EasyInvoice.entities.Order;
import com.luna.EasyInvoice.entities.Organisation;
import com.luna.EasyInvoice.pojo.StatisticPojo;
import com.luna.EasyInvoice.repository.InvoiceRepository;
import com.luna.EasyInvoice.service.InvoiceService;
import com.luna.EasyInvoice.utility.UtilitiesService;

@Service
public class InvoiceServiceImpl implements InvoiceService {

	@Autowired
	InvoiceRepository invoicerepository;
	
	@Autowired
	ArticleServImpl articleservice;
	
	@Autowired
	UtilitiesService utilitiesservice;
	
	@Override
	public Invoice save(Invoice invoice) {
		if(invoice!=null) return this.invoicerepository.save(invoice);
		else return null;
	}

	@Override
	//@PostFilter("hasRole('ADMIN') or filterObject.organisation == authentication.principal.organisation") 
	public Page<Invoice> fetch(int pageno, int pagesize) {
		Pageable pageable = PageRequest.of(pageno -1,pagesize, Sort.by("id").descending());
		if(this.utilitiesservice.checkIfHasRole("ROLE_ADMIN")) {
			return this.invoicerepository.findAll(pageable);
		}else {
			return this.invoicerepository.findByOrganisation(this.utilitiesservice.getOrganisationFromConnectedUser(),pageable);
		}
	}

	@Override
	public List<Invoice> fetch(Order order) {
		return this.invoicerepository.findByOrder(order, Sort.by("id").descending());
	}
	
	@Override
	public Invoice fetch(Long id) {
		System.out.println(">>>>>>>>>>> Received ID : "+id);
		Optional<Invoice> invoice = this.invoicerepository.findById(id);
		if(invoice.isPresent()) return invoice.get();
		else return null;
	}

	@Override
	public List<ArticleOrder> getArticleStatus(Order order) {
		//System.out.println(order);
		
		List<ArticleOrder> articless = new ArrayList<>();
		List<ArticleOrder> ob = order.getArticles();
		//System.out.println(ob);
		for(ArticleOrder a : ob) {
			double totalTakenQu= 0.0;
			double totalTakenAmount = 0.0;
			double vatAmount = 0.0;
			double totalTakenNVat = 0.0;
//			System.out.println("........ Quantity : "+a.getQuantity());
			List<ArticleInvoice> articles = a.getArticleInvoices();
			if(articles!=null) {
				if(articles.size()>0) {
					for(ArticleInvoice inv : articles) {
						totalTakenAmount = totalTakenAmount + inv.getTotal();
						totalTakenQu = totalTakenQu + inv.getQuantity();
						totalTakenNVat = totalTakenNVat + inv.getTotalNVat();
						vatAmount = vatAmount + inv.getTax();
					}
				}else {
					totalTakenQu = 0.0;
					totalTakenAmount = 0.0;
				}
				double remQte = a.getQuantity()-totalTakenQu;
				double remAmount = a.getTotal()-totalTakenAmount;
				double remVat = a.getTotalVAT() - vatAmount;
				double remNVat = a.getTotalNVAT() - totalTakenNVat;
//				System.out.println("Order qty : "+a.getQuantity());
//				System.out.println("totalTakenQu : "+totalTakenQu);
//				System.out.println("remQte : "+remQte);
//				
//				System.out.println("Order amount : "+a.getTotal());
//				System.out.println("totalTakenAmount : "+totalTakenAmount);
//				System.out.println("remAmount : "+remAmount);
//				
//				System.out.println("Order vat : "+a.getTotalVAT());
//				System.out.println("vatAmount : "+vatAmount);
//				System.out.println("remVat : "+remVat);
//				
//				System.out.println("Order TotalNvat : "+a.getTotalNVAT());
//				System.out.println("totalTakenNVat : "+totalTakenNVat);
//				System.out.println("remNVat : "+remNVat);
				if(remQte > 0 || remAmount > 0) {
					a.setQuantity(remQte);
					a.setTotal(remAmount);
					a.setTotalVAT(remVat);
					a.setTotalNVAT(remNVat);
					articless.add(a);
				}
//				System.out.println("............................... : articless : "+articless);
			}else {
//				System.out.println("++++++++++++++++++++++++++++++++ ");
				articless.add(a);
			}
		}
		return articless;
	}
	
	
	public List<ArticleOrder> getArticleStatus2(Order order) {
		
		List<ArticleOrder> articless = new ArrayList<>();
		for(ArticleOrder a : order.getArticles()) {
			double totalTakenQu= 0.0;
			double totalTakenAmount = 0.0;
			double vatAmount = 0.0;
			double totalTakenNVat = 0.0;
			
			List<ArticleInvoice> articles = a.getArticleInvoices();
			if(articles!=null) {
				if(articles.size()>0) {
					for(ArticleInvoice inv : articles) {
						totalTakenAmount = totalTakenAmount + inv.getTotal();
						totalTakenQu = totalTakenQu + inv.getQuantity();
						totalTakenNVat = totalTakenNVat + inv.getTotalNVat();
						vatAmount = vatAmount + inv.getTax();
					}
					
				}else {
					totalTakenQu = 0.0;
					totalTakenAmount = 0.0;
				}
				double remQte = a.getQuantity()-totalTakenQu;
				double  remAmount = a.getTotal()-totalTakenAmount;
				double remVat = a.getTotalVAT() - vatAmount;
				double remNVat = a.getTotalNVAT() - totalTakenNVat;				
				
//				System.out.println("Order qty : "+a.getQuantity());
//				System.out.println("totalTakenQu : "+totalTakenQu);
//				System.out.println("remQte : "+remQte);
//				
//				System.out.println("Order amount : "+a.getTotal());
//				System.out.println("totalTakenAmount : "+totalTakenAmount);
//				System.out.println("remAmount : "+remAmount);
//				
//				System.out.println("Order vat : "+a.getTotalVAT());
//				System.out.println("vatAmount : "+vatAmount);
//				System.out.println("remVat : "+remVat);
//				
//				System.out.println("Order TotalNvat : "+a.getTotalNVAT());
//				System.out.println("totalTakenNVat : "+totalTakenNVat);
//				System.out.println("remNVat : "+remNVat);
				
				
				if(remQte > 0 || remAmount > 0) {
					a.setQuantity(remQte);
					a.setTotal(remAmount);
					a.setTax(remVat);
					a.setTotalVAT(remVat);
					a.setTotalNVAT(remNVat);
					articless.add(a);
				}
			}
		}
//		System.out.println(articless);
		return articless;
	}

	@Override
	public List<Invoice> fetch(int status) {
		// TODO Auto-generated method stub
		return this.invoicerepository.findByStatus(status);
	}
	
	public List<Invoice> fetch(){
		return this.invoicerepository.findAll();
	}
	
	public List<StatisticPojo> countInvoice(){
		return this.invoicerepository.countInvoice();
	}
	
	public List<StatisticPojo> countInvoice(Organisation organisation){
		return this.invoicerepository.countInvoiceByOrganisation(organisation);
	} 
	
	
	public List<Invoice> getSearchedInvoice(Client client, List<Integer> status,  String validationref, String regNumber, Organisation org) {
		// TODO Auto-generated method stub
		return this.invoicerepository.findByClientAndStatusInAndValidationrefLikeAndReferenceNumberLikeAndOrganisation(client, status, validationref, regNumber, org);
	} 
	
	public List<Invoice> getSearchedInvoice(List<Integer>  status, String validationref, String regNumber, Organisation org) {
		// TODO Auto-generated method stub
		return this.invoicerepository.findByStatusInAndValidationrefLikeAndReferenceNumberLikeAndOrganisation(status,validationref,regNumber, org);
	} 
	
	
}
