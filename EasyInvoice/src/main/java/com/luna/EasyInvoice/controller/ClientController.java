package com.luna.EasyInvoice.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.luna.EasyInvoice.dto.TinResponse;
import com.luna.EasyInvoice.entities.Client;
import com.luna.EasyInvoice.entities.Organisation;
import com.luna.EasyInvoice.service.implementation.ClientServImpl;
import com.luna.EasyInvoice.service.implementation.OBRSendingServiceImplementation;
import com.luna.EasyInvoice.utility.Response;
import com.luna.EasyInvoice.utility.UtilitiesService;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class ClientController {
	
	
	final private Environment env;
	final private UtilitiesService utilityservice;
	final private ClientServImpl clientservice;
	final private OBRSendingServiceImplementation obrservice;
	
	@GetMapping("/clients")
	public String index(Model model) {
		return this.index(1, model);
	}
	
	@GetMapping("/clients/{pageno}")
	public String index(@PathVariable() int pageno, Model model) {
		int pagesize = Integer.valueOf(this.env.getProperty("config.pagesize"));
		Page<Client> clients = this.clientservice.getAllClientRefPaged(pageno, pagesize);
		model.addAttribute("clients", clients.getContent());
		model.addAttribute("currentPage", pageno);
		model.addAttribute("totalpages",clients.getTotalPages());
		model.addAttribute("size", pagesize);
		model.addAttribute("jsName", "super.js"); //super.js
		return "administration/clients";
	}
	
	@PostMapping("/saveClient")
	public @ResponseBody Response save(
			@RequestParam String cli_name,
			@RequestParam String cli_contact_person, 
			@RequestParam String cli_tin, 
			@RequestParam String cli_telephon,
			@RequestParam String cli_email, 
			@RequestParam String cli_vat_sub,
			@RequestParam String commune,
			@RequestParam String province,
			@RequestParam String district,
			@RequestParam String type,
			@RequestParam String sigle
			) {
		
		Organisation organisation = this.utilityservice.getOrganisationFromConnectedUser();
		
		Response response = new Response();
		Client client = new Client();
		if(organisation!=null) {
			client.setOrganisation(organisation);
		}
		//client.setAddInfos(cli_address);
		client.setCommune(commune);
		client.setProvince(province);
		client.setQuartier(district);
		client.setTypeClient(type);
		client.setAbbr(sigle);
		client.setContactPerson(cli_contact_person);
		client.setEmail(cli_email);
		client.setName(cli_name);
		client.setRegistrationDate(new Date());
		client.setTelephone(cli_telephon);
		client.setTIN(cli_tin);
		if(cli_vat_sub.equalsIgnoreCase("true")) {
			client.setVatSubject(true);
		}else {
			client.setVatSubject(false);
		}
		client = this.clientservice.save(client);
		List<Object> obj = new ArrayList<>();
		obj.add(client);
		response.setCode(1);
		response.setBody(obj);
		response.setDescription("SUCCESS");
		return response;
	}
	
	@PostMapping("/findClient")
	public @ResponseBody Response search(
			@RequestParam String id
			) {
		Response response = new Response();
		Client client = this.clientservice.findSingleClient(Long.valueOf(id));
		response.setCode(1);
		response.setDescription("Success");
		List<Object> obj = new ArrayList<>();
		obj.add(client);
		response.setBody(obj);
		return response;
	}
	
	@PostMapping("/getClientByTelephone")
	public @ResponseBody Response search(HttpServletRequest request, @RequestParam String telephone) {
		Response response = new Response();
		
		Client client = this.clientservice.findClient(telephone);
		if(client!=null) {
			List<Object> obj = new ArrayList<>();
			obj.add(client);
			response.setCode(1);
			response.setBody(obj);
		}else {
			response.setCode(0);
		}
		System.out.println(client);
		return response;
	}
	
	@PostMapping("/getClientByName")
	public @ResponseBody Response searchByName(HttpServletRequest request, @RequestParam String name) {
		Response response = new Response();
		
		Client client = this.clientservice.findClientByName(name);
		if(client!=null) {
			List<Object> obj = new ArrayList<>();
			obj.add(client);
			response.setCode(1);
			response.setBody(obj);
		}else {
			response.setCode(0);
		}
		System.out.println(client);
		return response;
	}
	
	@PostMapping("/updateClient")
	public @ResponseBody Response updateClient(
			@RequestParam String client_id,
			@RequestParam String vat_subj, 
			@RequestParam String province, 
			@RequestParam String commune, 
			@RequestParam String district, 
			@RequestParam String email, 
			@RequestParam String telephone, 
			@RequestParam String tin, 
			@RequestParam String contact_person, 
			@RequestParam String nam,
			@RequestParam String typeClient,
			@RequestParam String sigle
			) {
		Response response = new Response();
		Client client = this.clientservice.findSingleClient(Long.valueOf(client_id));
		//client.setAddInfos(address);
		client.setProvince(province);
		client.setCommune(commune);
		client.setQuartier(district);
		client.setContactPerson(contact_person);
		client.setEmail(email);
		client.setName(nam);
		client.setTelephone(telephone);
		client.setTIN(tin);
		client.setTypeClient(typeClient);
		client.setAbbr(sigle);
		
		if(vat_subj.equalsIgnoreCase("true")) {
			client.setVatSubject(true);
		}else {
			client.setVatSubject(false);
		}
		this.clientservice.save(client);
		response.setCode(1);
		
		response.setDescription("success");
		return response;
	}
	
	@PostMapping("/checkTin")
	public @ResponseBody Response checkClientTin(@RequestParam String tin) throws Exception {
		Response response = new Response();
		Organisation org = this.utilityservice.getOrganisationFromConnectedUser();
		String username = org.getInterconnections().get(0).getUsername();
		String password = org.getInterconnections().get(0).getPassword();
		String url 		= org.getInterconnections().get(0).getUrl();
		try {
			TinResponse result = this.obrservice.checkTin(tin, url, username, password);
			if(result.isSuccess()) {
				response.setCode(1);
				response.setDescription("SUCESS");
				List<Object> obj = new ArrayList<>();
				obj.add(result.getResult().getTaxpayer().get(0).getName());
				response.setBody(obj);
			}else {
				response.setDescription("NIF non trouve");
				response.setCode(0);
			}
		} catch (Exception ex) {
			response.setDescription("Echec de recuperation du NIF avec l'erreur suivant :"+ex.getMessage());
			response.setCode(0);
		}
		return response;
	}
}
