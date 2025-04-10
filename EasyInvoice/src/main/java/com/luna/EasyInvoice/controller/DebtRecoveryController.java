package com.luna.EasyInvoice.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.luna.EasyInvoice.dto.DebtRenderDTO;
import com.luna.EasyInvoice.dto.Mouvement;
import com.luna.EasyInvoice.entities.Client;
import com.luna.EasyInvoice.entities.DebtRecovery;
import com.luna.EasyInvoice.service.ClientService;
import com.luna.EasyInvoice.service.implementation.DebtRecoveryService;
import com.luna.EasyInvoice.utility.Response;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class DebtRecoveryController {
	
	final private DebtRecoveryService debtRecoveryService;
	final private ClientService clientService;
	
	@RequestMapping("/debts")
	public String index(Model model) {
		List<DebtRenderDTO> debts = this.debtRecoveryService.getStatus();
		System.out.println("Size debt : "+ debts.size());
		double globalTotal =0.0;
		double globalratte= 0.0;
		double globalPaid = 0.0;
		double globalUnpaid = 0.0;
		if(debts.size()>0) {
			for(DebtRenderDTO de: debts) {
				globalTotal = globalTotal + de.getDebtAmount();
				globalPaid = globalPaid + de.getPaidAmount();
			}
			globalUnpaid = globalTotal - globalPaid;
			globalratte = globalPaid*100/globalTotal;
		}
		model.addAttribute("rec_glo_rates", Math.round(globalratte) );
		model.addAttribute("rec_glo_amount", Math.round(globalTotal));
		model.addAttribute("rec_glo_paid", Math.round(globalPaid));
		model.addAttribute("rec_glo_recover", Math.round(globalUnpaid));
		
		model.addAttribute("debtss", debts);
		model.addAttribute("debts", this.debtRecoveryService.fetch());
		model.addAttribute("jsName", "super.js"); //super.js
		return "debt/debts";
	}
	
	
	@PostMapping("/searchDebtInvoice")
	public @ResponseBody Response searchClientNameAndAmount(@RequestParam String id) {
		Response response = new Response ();
		DebtRecovery debt = this.debtRecoveryService.getById(Long.valueOf(id));
		List<Object> obj = new ArrayList<>();
		obj.add(debt.getAmount());
		obj.add(debt.getInvoice().getClient().getName());
		response.setCode(1);
		response.setBody(obj);
		return response;
	}
	@GetMapping("/checkClient/{iClient}")
	public String checkClient(@PathVariable() int iClient, Model model) {
//		ReportSearch report = new ReportSearch();
//		model.addAttribute("report",report);
		Client client = this.clientService.findSingleClient(Long.valueOf(iClient));
		model.addAttribute("client_name", client.getName());
		model.addAttribute("client_tin", client.getTIN());
		model.addAttribute("client_registration_date", client.getRegistrationDate());
		
		List<Mouvement> mouvements = this.debtRecoveryService.getStatus(client);
		double totalDebt = 0.0;
		double totalPayment = 0.0;
		for(Mouvement mvt : mouvements) {
			totalDebt = totalDebt + mvt.getCredit();
			totalPayment = totalPayment + mvt.getDebit();
		}
		
		Collections.sort(mouvements);
		
		
		model.addAttribute("glob_amount", totalDebt);
		model.addAttribute("paid_amount", totalPayment);
		model.addAttribute("rem_amount", totalDebt - totalPayment);
		model.addAttribute("jsName", "super.js"); //super.js
		model.addAttribute("mouvements", mouvements);
		
		return "debt/debtstatus";
	}
	
	

}
