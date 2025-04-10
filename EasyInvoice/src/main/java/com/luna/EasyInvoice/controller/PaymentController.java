package com.luna.EasyInvoice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.luna.EasyInvoice.dto.DebtRecoveryDTO;
import com.luna.EasyInvoice.dto.PaymentDTAO;
import com.luna.EasyInvoice.service.implementation.DebtRecoveryService;
import com.luna.EasyInvoice.service.implementation.PaymentService;
import com.luna.EasyInvoice.utility.Response;
import com.luna.EasyInvoice.utility.UtilitiesService;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class PaymentController {

	final private DebtRecoveryService debtRecoveryService;
	final private UtilitiesService utilitiesService;
//	final private EnvironmentConfig environmentconfig;
//	final private ActionService actionService;
	final private PaymentService paymentService;
	
	@RequestMapping("/payments")
	
	public String index(Model model) {
		model.addAttribute("debts", debtRecoveryService.fetch());
		model.addAttribute("jsName", "super.js"); //super.js
		model.addAttribute("pays", this.paymentService.fetch());
		return "recovery/payments";
	}
	
	
	@PostMapping("/savepayment")
	public @ResponseBody Response save(@RequestParam String pay_invoice,
			@RequestParam String pay_date,
			@RequestParam String pay_type,
			@RequestParam String pay_reference,
			@RequestParam String pay_bank,
			@RequestParam String pay_bank_account,
			@RequestParam String pay_comment,
			@RequestParam String pay_amount) {
		
		Response response = new Response();
		PaymentDTAO pay = new PaymentDTAO();
		DebtRecoveryDTO debt = this.debtRecoveryService.get(Long.valueOf(pay_invoice));
		pay.setDebt(debt.getId());
		
		pay.setAmount(Double.valueOf(pay_amount));
		pay.setBankAccount(pay_bank_account);
		pay.setBankName(pay_bank);
		pay.setComments(pay_comment);
		
		pay.setPaidMode(Integer.valueOf(pay_type));
		pay.setPaymentDate(this.utilitiesService.getdateFromString(pay_date));
		pay.setPaymentReference(pay_reference);
		pay.setTotalpaid(Double.valueOf(pay_amount));
		pay.setTotalvat(0);
		this.paymentService.create(pay);
		response.setCode(1);
		return response;
	}
}
