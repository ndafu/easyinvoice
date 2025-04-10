package com.luna.EasyInvoice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.luna.EasyInvoice.config.EnvironmentConfig;
import com.luna.EasyInvoice.dto.ActionDTO;
import com.luna.EasyInvoice.dto.DebtRecoveryDTO;
import com.luna.EasyInvoice.service.implementation.ActionService;
import com.luna.EasyInvoice.service.implementation.DebtRecoveryService;
import com.luna.EasyInvoice.utility.Response;
import com.luna.EasyInvoice.utility.UtilitiesService;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class ActionController {
	final private DebtRecoveryService debtRecoveryService;
	final private UtilitiesService utilitiesService;
//	final private EnvironmentConfig environmentconfig;
	final private ActionService actionService;

	@RequestMapping("/actions")
	public String index(Model model) {
		model.addAttribute("invoices", debtRecoveryService.fetchUnpaid());
		model.addAttribute("actions", actionService.fetch());
		model.addAttribute("jsName", "super.js"); 
		return "recovery/actions";
	}
	
	@PostMapping("/saveAction")
	public @ResponseBody Response save (
			@RequestParam String invoice,
			@RequestParam String client,
			@RequestParam String amount,
			@RequestParam String rec_date,
			@RequestParam String means,
			@RequestParam String contactPerson,
			@RequestParam String telephone,
			@RequestParam String email,
			@RequestParam String result,
			@RequestParam String comments,
			@RequestParam String next,
			@RequestParam String recover) {
		Response response = new Response();
		
		DebtRecoveryDTO debt = this.debtRecoveryService.get(Long.valueOf(invoice));
		ActionDTO action = new ActionDTO();
		action.setActionDate(this.utilitiesService.getdateFromString(rec_date));
		action.setComments(comments);
		action.setContactedEmail(email);
		action.setContactedPerson(contactPerson);
		action.setContactedTelephone(telephone);
		action.setCreatedBy(this.utilitiesService.getUsername());
		action.setDebt(debt.getId());
		action.setResult(result);
		action.setType(Integer.valueOf(means));
		action.setUsedMean(Integer.valueOf(means));
		if(next.isEmpty()) {
			int days = 7;
			action.setNextAppointment(this.utilitiesService.dateAddition(action.getActionDate(), days));
		}else {
			action.setNextAppointment(this.utilitiesService.getdateFromString(next));
		}
		action.setWhoExecuted(recover);
		this.actionService.create(action);
		response.setCode(1);
		return response;
	}
}
