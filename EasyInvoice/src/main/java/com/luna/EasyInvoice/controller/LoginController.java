package com.luna.EasyInvoice.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;  
import org.springframework.security.core.Authentication;  
import org.springframework.security.core.context.SecurityContextHolder;  
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;  
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.luna.EasyInvoice.utility.UtilitiesService;


@Controller 
public class LoginController {
	@Autowired
	UtilitiesService utilitieservice;
	
	@RequestMapping(value = "login")
	public String loginPage() {
		if(this.utilitieservice.checkIfConnected()) {
			return "/dashboard";
		}else {
			return "login";
		}
	}
	
	@RequestMapping(value = "login_error")
	public String loginErrorPage(Model model) {
		model.addAttribute("loginError",true);
		model.addAttribute("jsName", "super.js"); 
		return "login";
	}
	@RequestMapping(value = "/logout", method=RequestMethod.GET)
	public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();  
		if (auth != null){      
           new SecurityContextLogoutHandler().logout(request, response, auth);  
        }  
		return "redirect:/login";
	}
	
	@GetMapping("/error")
	public String getError() {
		System.out.println("____________________________");
		return "error";
	}
	
	@RequestMapping(value="/resp", produces = { MediaType.ALL_VALUE,//
            MediaType.APPLICATION_XML_VALUE })
    @ResponseBody
    public String welcome() {
        return "Welcome to RestTemplate Example.";
    }
}
