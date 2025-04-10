package com.luna.EasyInvoice.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.luna.EasyInvoice.entities.Organisation;
import com.luna.EasyInvoice.entities.Role;
import com.luna.EasyInvoice.entities.User;
import com.luna.EasyInvoice.repository.RoleRepository;
import com.luna.EasyInvoice.service.implementation.OrganisationServImpl;
import com.luna.EasyInvoice.service.implementation.UserPrincipal;
import com.luna.EasyInvoice.service.implementation.UserServiceImpl;
import com.luna.EasyInvoice.utility.Response;
import com.luna.EasyInvoice.utility.UtilitiesService;

@Controller
public class UserController {
	@Autowired
	RoleRepository rolerepository;
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	
	
	@Autowired
	OrganisationServImpl organisation;
	
	Response rsp = new Response();
	
	@Autowired
	UserServiceImpl userrepo;	
	
	@Autowired
	UtilitiesService utilityservice;

	
	@RequestMapping("/users")
	public String index(Model model) {
		
		return this.fetch(1, model);
		
	}
	
	@RequestMapping(value="/users/{pageno}")
	public String fetch(@PathVariable() int pageno, Model model) {
		int pagesize = 25;
		Page<User> pages = userrepo.fetchUsers(pageno, pagesize);
		
		List<User> listusers = pages.getContent();
		if(!this.utilityservice.checkIfHasRole("ROLE_ADMIN")) {
			System.out.println("here we are ...");
			model.addAttribute("organisation", this.utilityservice.getConnectedUserInfo().getOrganisation());
		}else {
			System.out.println("here we are again ...");
			model.addAttribute("organisation", "");
		}
		model.addAttribute("users", listusers);
		model.addAttribute("currentPage", pageno);
		model.addAttribute("totalpages",pages.getTotalPages());
		model.addAttribute("totalItems",pages.getTotalElements());
		model.addAttribute("size", listusers.size());
		model.addAttribute("organisations", this.organisation.findOrganisation());
		model.addAttribute("jsName", "super.js"); //super.js
		return "users/user";
	}
	
	@RequestMapping("/saveuser")
	public @ResponseBody Response saveuser(
			@RequestParam String username,
			@RequestParam String status,
			@RequestParam String password,
			@RequestParam String firstName,
			@RequestParam String lastName,
			@RequestParam String organisation,
			@RequestParam String telephone,
			@RequestParam String email,
			@RequestParam String enabled,
			@RequestParam String tokenExpired,
			@RequestParam String adminrole,
			@RequestParam String com_manager,
			@RequestParam String com_head_admin,
			@RequestParam String com_head_fina,
			@RequestParam String com_log_officer,
			@RequestParam String com_hr_off,
			@RequestParam String com_cash,
			@RequestParam String com_account
			) {
		Response rsp = new Response();
		List<Role> roles = new ArrayList<>();
		
		//roles
		if(adminrole.equalsIgnoreCase("true")) {
			Role adminRole = rolerepository.findByName("ROLE_ADMIN");
			roles.add(adminRole);
		}
		if(com_manager.equalsIgnoreCase("true")) {
			Role stockRole = rolerepository.findByName("ROLE_COM_MANAGER");
			roles.add(stockRole);
		}
		
		if(com_head_admin.equalsIgnoreCase("true")) {
			Role stockRole = rolerepository.findByName("ROLE_COM_HEAD_ADMIN");
			roles.add(stockRole);
		}
		if(com_head_fina.equalsIgnoreCase("true")) {
			Role stockRole = rolerepository.findByName("ROLE_COM_HEAD_FINA");
			roles.add(stockRole);
		}
		if(com_log_officer.equalsIgnoreCase("true")) {
			Role stockRole = rolerepository.findByName("ROLE_COM_LOG_OFF");
			roles.add(stockRole);
		}
		if(com_hr_off.equalsIgnoreCase("true")) {
			Role stockRole = rolerepository.findByName("ROLE_COM_HR_OFF");
			roles.add(stockRole);
		}
		if(com_cash.equalsIgnoreCase("true")) {
			Role stockRole = rolerepository.findByName("ROLE_COM_CASH");
			roles.add(stockRole);
		}
		if(com_account.equalsIgnoreCase("true")) {
			Role stockRole = rolerepository.findByName("ROLE_COM_ACOUNTANT");
			roles.add(stockRole);
		}
		
		//Get the user info
		UserPrincipal princ = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();  
		//System.out.println("User : "+auth.toString());
		String createdBy = princ.getUserName();
		
		//String org = princ.getOrganisation();
		//
		
		User user = new User();
		if(enabled.equalsIgnoreCase("true")) {
			user.setEnabled(true);
		}else {
			user.setEnabled(false);
		}
		if(tokenExpired.equalsIgnoreCase("true")) {
			user.setTokenExpired(true);
		}else {
			user.setTokenExpired(false);
		}
		
		user.setCreatedBy(createdBy);
		//user.setOrganisation(org);
		user.setEmail(email);
		//user.setEnabled(false);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setOrganisation(organisation);
		user.setOrganisationName(this.organisation.findSingleOrganisation(Long.valueOf(organisation)).getName());
		user.setPassword(passwordEncoder.encode(password));
		user.setStatus(1);
		user.setTelephone(telephone);
		//user.setTokenExpired(false);
		user.setUsername(username);
		user.setRoles(roles);
		
		user.setCreatedOn(new Date());
		
		user = userrepo.saveUser(user);
		
		if(user==null) {
			rsp.setCode(-1);
			rsp.setDescription("Error while saving user");
		}else {
			rsp.setCode(1);
			rsp.setDescription("success");
			List<Object> obj = new ArrayList<>();
			obj.add(user);
			rsp.setBody(obj);
		}
		return rsp;
	}
	
	@RequestMapping("/finduser")
	public @ResponseBody Response find(
			@RequestParam int id
			) {
		//Response rsp = new Response();
		User user = this.userrepo.finduserbyid(id);
		if(user!=null) {
			rsp.setCode(1);
			rsp.setDescription("Success");
			List<Object> obj = new ArrayList<>();
			obj.add(user);
			rsp.setBody(obj);
		}else {
			rsp.setCode(0);
			rsp.setDescription("No user found");
		}
		return rsp;
	}
	
	@RequestMapping("/edituser")
	public @ResponseBody Response edituser(
			@RequestParam String username,
			String status,
			String password,
			String firstName,
			String lastName,
			String organisation,
			String telephone,
			String email,
			String enabled,
			String tokenExpired,
			String adminrole,
			@RequestParam String id,
			@RequestParam String com_manager, 
			@RequestParam String com_head_admin, 
			@RequestParam String com_head_fina, 
			@RequestParam String com_log_officer, 
			@RequestParam String com_hr_off, 
			@RequestParam String com_cash, 
			@RequestParam String com_account
			) {
		User user = this.userrepo.finduserbyid(Integer.valueOf(id));

			if(user==null) {
				rsp.setCode(0);
				rsp.setDescription("No user found for the username");
			}else {
				List<Role> roles = new ArrayList<>();
				
				if(adminrole.equalsIgnoreCase("true")) {
					Role adminRole = rolerepository.findByName("ROLE_ADMIN");
					roles.add(adminRole);
				}
				if(com_manager.equalsIgnoreCase("true")) {
					Role stockRole = rolerepository.findByName("ROLE_COM_MANAGER");
					roles.add(stockRole);
				}
				
				if(com_head_admin.equalsIgnoreCase("true")) {
					Role stockRole = rolerepository.findByName("ROLE_COM_HEAD_ADMIN");
					roles.add(stockRole);
				}
				if(com_head_fina.equalsIgnoreCase("true")) {
					Role stockRole = rolerepository.findByName("ROLE_COM_HEAD_FINA");
					roles.add(stockRole);
				}
				if(com_log_officer.equalsIgnoreCase("true")) {
					Role stockRole = rolerepository.findByName("ROLE_COM_LOG_OFF");
					roles.add(stockRole);
				}
				if(com_hr_off.equalsIgnoreCase("true")) {
					Role stockRole = rolerepository.findByName("ROLE_COM_HR_OFF");
					roles.add(stockRole);
				}
				if(com_cash.equalsIgnoreCase("true")) {
					Role stockRole = rolerepository.findByName("ROLE_COM_CASH");
					roles.add(stockRole);
				}
				if(com_account.equalsIgnoreCase("true")) {
					Role stockRole = rolerepository.findByName("ROLE_COM_ACOUNTANT");
					roles.add(stockRole);
				}
				if(enabled.equalsIgnoreCase("true")) {
					user.setEnabled(true);
				}else {
					user.setEnabled(false);
				}
				if(tokenExpired.equalsIgnoreCase("true")) {
					user.setTokenExpired(true);
				}else {
					user.setTokenExpired(false);
				}
				
				user.setEmail(email);
				//user.setEnabled(false);
				user.setFirstName(firstName);
				user.setLastName(lastName);
				user.setOrganisation(organisation);
				if(!password.isEmpty()) {
					user.setPassword(passwordEncoder.encode(password));
				}
				user.setStatus(1);
				user.setTelephone(telephone);
				user.setUsername(username);
				user.setRoles(roles);
				user = userrepo.saveUser(user);
				if(user==null) {
					rsp.setCode(-10);
					rsp.setDescription("Error while saving user");
				}else {
					rsp.setCode(1);
					rsp.setDescription("success");
					List<Object> obj = new ArrayList<>();
					obj.add(user);
					rsp.setBody(obj);
				}
			}
		
		return rsp;
	}
	
	@PostMapping("/getProfil")
	public @ResponseBody Response getProfil() {
		Response response = new Response();
		UserPrincipal princ = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();  
		//String createdBy = princ.getUserName();
		//String org = princ.getOrganisation();
		User user = this.userrepo.findeuserbyUsername(princ.getUsername());
		
		
		if(this.utilityservice.isStringInt(user.getOrganisation())) {
			user.setOrganisation(this.organisation.findSingleOrganisation(Long.valueOf(user.getOrganisation())).getName());
		}
		response.setCode(1);
		response.setDescription("SUCCESS");
		List<Object> obj = new ArrayList<>();
		obj.add(user);
		response.setBody(obj);
		
		System.out.println(user.toString());
		return response;
	}
	
	@PostMapping("/updateProfil")
	public @ResponseBody Response updateProfil(
			@RequestParam String username,
			@RequestParam String last_name,
			@RequestParam String first_name,
			@RequestParam String email,
			@RequestParam String phone,
			@RequestParam String pass
			) {
		Response response = new Response();
		User user = this.userrepo.findeuserbyUsername(username);
		if(!pass.isEmpty()) {
			user.setPassword(passwordEncoder.encode(pass));
		}
		user.setEmail(email);
		user.setFirstName(first_name);
		user.setLastName(last_name);
		user.setTelephone(phone);
		this.userrepo.saveUser(user);
		response.setCode(1);
		response.setDescription("SUCCESS");
		return response;
	}
}
