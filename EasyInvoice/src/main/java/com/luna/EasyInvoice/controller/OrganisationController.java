package com.luna.EasyInvoice.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.luna.EasyInvoice.dto.ResponsePojo;
import com.luna.EasyInvoice.dto.ResultPojo;
import com.luna.EasyInvoice.entities.Interconnection;
import com.luna.EasyInvoice.entities.Organisation;
import com.luna.EasyInvoice.pojo.InterconnectionPojo;
import com.luna.EasyInvoice.pojo.OrganisationPojo;
import com.luna.EasyInvoice.service.implementation.InterconnectionServiceImpl;
import com.luna.EasyInvoice.service.implementation.OBRSendingServiceImplementation;
import com.luna.EasyInvoice.service.implementation.OrganisationServImpl;
import com.luna.EasyInvoice.utility.Response;


@Controller
public class OrganisationController {
	@Autowired
	OrganisationServImpl organisationservice;
	
	@Autowired
	OBRSendingServiceImplementation obrutility;
	
	@Autowired
	InterconnectionServiceImpl interconnectionservice;
	
	@PreAuthorize("isAuthenticated() and hasRole('ADMIN')")
	@GetMapping("/myCompany")
	public String index(Model model) {
		List<Organisation> organisations = this.organisationservice.findOrganisation();
		model.addAttribute("organisations", organisations);
		model.addAttribute("sizeOrganisation", organisations.size());
		model.addAttribute("jsName", "super.js"); //super.js
		return "administration/organisation";
	}
	@PostMapping("/getOrganisation")
	public @ResponseBody Response fetch(@RequestParam String id){
		Response response = new Response();
		Organisation organisation = this.organisationservice.findSingleOrganisation(Long.valueOf(id));
		OrganisationPojo org = new OrganisationPojo();
		org.setId(organisation.getId());
		org.setAccountTitle(organisation.getAccountTitle());
		org.setActivitySecter(organisation.getActivitySecter());
		org.setAddressAvenue(organisation.getAddressAvenue());
		org.setAddressCommune(organisation.getAddressCommune());
		org.setAddressNumber(organisation.getAddressNumber());
		org.setAddressProvince(organisation.getAddressProvince());
		org.setAddressQuartier(organisation.getAddressQuartier());
		org.setAdress(organisation.getAdress());
		org.setBankAccount(organisation.getBankAccount());
		org.setBankName(organisation.getBankName());
		org.setCTPayer(organisation.isCTPayer());
		org.setFiscalCenter(organisation.getFiscalCenter());
		
		org.setJuridictionForm(organisation.getJuridictionForm());
		org.setLogo(organisation.getLogo());
		org.setLVPayer(organisation.isLVPayer());
		org.setName(organisation.getName());
		org.setPostalNumber(organisation.getPostalNumber());
		org.setRepresentativeName(organisation.getRepresentativeName());
		org.setRepresentativePosition(organisation.getRepresentativePosition());
		org.setTelephone(organisation.getTelephone());
		org.setTin(organisation.getTin());
		org.setTradeNumber(organisation.getTradeNumber());
		org.setVATPayer(organisation.isVATPayer());
		List<InterconnectionPojo> interconnections = new ArrayList<>();
		for(Interconnection i : organisation.getInterconnections()) {
			InterconnectionPojo ii = new InterconnectionPojo();
			ii.setPassword(i.getPassword());
			ii.setUrl(i.getUrl());
			ii.setUsername(i.getUsername());
			interconnections.add(ii);
		}
		org.setIntercons(interconnections);
		//organisation.geti
		System.out.println(org);
		response.setCode(1);
		response.setDescription("SUCCESS");
		List<Object> obj = new ArrayList<>();
		obj.add(org);
		response.setBody(obj);
		return response;
	}
	@Transactional
	@PreAuthorize("isAuthenticated() and hasRole('ADMIN')")
	@PostMapping("/saveCompany")
	public @ResponseBody Response save(
				@RequestParam String isVatSubjected,
				@RequestParam String com_more_info ,
				@RequestParam String com_jur_form, 
				@RequestParam String com_activity_sector, 
				@RequestParam String com_fiscal_center, 
				@RequestParam String com_tin, 
				@RequestParam String com_name,
				@RequestParam String account_title,
				@RequestParam String bank_account, 
				@RequestParam String bank_name,
				@RequestParam String tc_subj,
				@RequestParam String lv_subj,
				@RequestParam String com_repr_name,
				@RequestParam String com_repr_position,
				@RequestParam String trade_num, 
				@RequestParam String com_province,
				@RequestParam String com_commune, 
				@RequestParam String com_district, 
				@RequestParam String com_street, 
				@RequestParam String com_street_number, 
				@RequestParam String com_postal_number,
				@RequestParam String status,
				@RequestParam String url,
				@RequestParam String username,
				@RequestParam String password,
				@RequestParam String com_telephone,
				@RequestParam String logo
			) {
		Response response = new Response();
		Organisation organisation = new Organisation();
		if(isVatSubjected.equalsIgnoreCase("true")) {
			organisation.setVATPayer(true);
		}else {
			organisation.setVATPayer(false);
		}
		
		if(lv_subj.equalsIgnoreCase("true")) {
			organisation.setLVPayer(true);
		}else {
			organisation.setLVPayer(false);
		}
		
		if(tc_subj.equalsIgnoreCase("true")) {
			organisation.setCTPayer(true);
		}else {
			organisation.setCTPayer(false);
		}

		organisation.setTradeNumber(trade_num);
		organisation.setAddressQuartier(com_district);
		organisation.setAddressProvince(com_province);
		organisation.setAddressAvenue(com_street);
		organisation.setAddressCommune(com_commune);
		organisation.setAddressNumber(com_street_number);
		organisation.setPostalNumber(com_postal_number);
		organisation.setTelephone(com_telephone);
		organisation.setRepresentativeName(com_repr_name);
		organisation.setRepresentativePosition(com_repr_position);
		organisation.setAccountTitle(account_title);
		organisation.setBankAccount(bank_account);
		organisation.setBankName(bank_name);
		organisation.setActivitySecter(com_activity_sector);
		organisation.setAdress(com_more_info);
		organisation.setFiscalCenter(com_fiscal_center);
		organisation.setJuridictionForm(com_jur_form);
		organisation.setName(com_name);
		organisation.setTin(com_tin);
		organisation.setLogo(logo);
		this.organisationservice.save(organisation);
		if(status.equalsIgnoreCase("true")) {
			Interconnection interconnection = new Interconnection();
			interconnection.setCreatedDate(new Date());
			interconnection.setLastModified(new Date());
			interconnection.setName("OBR EBMS");
			interconnection.setOrganisation(organisation);
			interconnection.setPassword(password);
			interconnection.setUrl(url);
			interconnection.setStatus(1);
			interconnection.setUsername(username);
			this.interconnectionservice.save(interconnection);
		}
		response.setCode(1);
		response.setDescription("SUCCESS");
		return response;
	}
	@Transactional
	@PostMapping("/updateO")
	public @ResponseBody Response updateOrganisation(
			@RequestParam String com_more_info,
			@RequestParam String com_jur_form,
			@RequestParam String com_activity_sector,
			@RequestParam String com_fiscal_center,
			@RequestParam String com_tin,
			@RequestParam String com_name,
			@RequestParam String account_title,
			@RequestParam String bank_account,
			@RequestParam String bank_name,
			@RequestParam String com_repr_name,
			@RequestParam String com_repr_position,
			@RequestParam String trade_num,
			@RequestParam String com_province,
			@RequestParam String com_commune,
			@RequestParam String com_district,
			@RequestParam String com_street,
			
			@RequestParam String status,
			@RequestParam String url,
			@RequestParam String username,
			@RequestParam String password,
			
			@RequestParam String com_street_num,
			@RequestParam String com_postal_num,
			@RequestParam String com_telephone,
			@RequestParam String com_logo,
			@RequestParam String vat_subj,
			@RequestParam String lv_subj,
			@RequestParam String tc_subj,
			@RequestParam String com_id
			) {
		Response response = new Response();
		//System.out.println(com_id);
		Organisation organisation = this.organisationservice.findSingleOrganisation(Long.valueOf(com_id));
		organisation.setLogo(com_logo);
		organisation.setAccountTitle(account_title);
		organisation.setActivitySecter(com_activity_sector);
		organisation.setAddressAvenue(com_street);
		organisation.setAddressCommune(com_commune);
		organisation.setAddressNumber(com_street_num);
		organisation.setAddressProvince(com_province);
		organisation.setAddressQuartier(com_district);
		organisation.setAdress(com_more_info);
		organisation.setBankAccount(bank_account);
		organisation.setBankName(bank_name);
		organisation.setFiscalCenter(com_fiscal_center);
		organisation.setJuridictionForm(com_jur_form);
		organisation.setName(com_name);
		organisation.setPostalNumber(com_postal_num);
		organisation.setRepresentativeName(com_repr_name);
		organisation.setRepresentativePosition(com_repr_position);
		organisation.setTelephone(com_telephone);
		organisation.setTin(com_tin);
		organisation.setTradeNumber(trade_num);
		
		if(vat_subj.equalsIgnoreCase("true")) {
			organisation.setVATPayer(true);
		}else {
			organisation.setVATPayer(false);
		}
		if(tc_subj.equalsIgnoreCase("true")) {
			organisation.setCTPayer(true);
		}else {
			organisation.setCTPayer(false);
		}
		
		if(lv_subj.equalsIgnoreCase("true")) {
			organisation.setLVPayer(true);
		}else {
			organisation.setLVPayer(false);
		}
		organisation = this.organisationservice.save(organisation);
		List<Interconnection> interc = organisation.getInterconnections();
		if(status.equalsIgnoreCase("true") && interc.size()<=0) {
			Interconnection i = new Interconnection();
			i.setCreatedDate(new Date());
			i.setLastModified(new Date());
			i.setName("OBR EBMS");
			i.setOrganisation(organisation);
			i.setPassword(password);
			i.setStatus(1);
			i.setUrl(url);
			i.setUsername(username);
			this.interconnectionservice.save(i);
		}else if(status.equalsIgnoreCase("true") && interc.size()>0) {
			for(Interconnection i : interc) {
				i.setLastModified(new Date());
				i.setUrl(url);
				i.setPassword(password);
				i.setUsername(username);
				this.interconnectionservice.save(i);
			}
		}else if(status.equalsIgnoreCase("false") && interc.size()>0) {
			for(Interconnection i : interc) {
				this.interconnectionservice.delete(i);
			}
		}
		response.setCode(1);
		response.setDescription("SUCCESS");
		return response;
	}
	
	@PostMapping("/isOBRChecked")
	public @ResponseBody Response isObrChecked(
				@RequestParam String url,
				@RequestParam String username,
				@RequestParam String password
			) throws Exception {
		Response response = new Response();
		ResponsePojo resp = this.obrutility.getConnected(url, username, password);
		System.out.println(resp.toString());
		if(resp.isSuccess()) {
			response.setCode(1);
			response.setDescription("CREDENTIALS ARE OK");
		}else {
			response.setCode(0);
			response.setDescription("CREDENTIALS ARE NOT OK");
		}
		return response;
	}
	
}
