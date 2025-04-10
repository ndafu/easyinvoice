package com.luna.EasyInvoice.service.implementation;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.luna.EasyInvoice.dto.PaymentDTAO;
import com.luna.EasyInvoice.entities.DebtRecovery;
import com.luna.EasyInvoice.entities.Payment;
import com.luna.EasyInvoice.repository.DebtRecoveryRepository;
import com.luna.EasyInvoice.repository.PaymentRepository;
import com.luna.EasyInvoice.utility.NotFoundException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PaymentService {

	final private PaymentRepository paymentRepository;
	final private DebtRecoveryRepository debtrepository;

    public List<PaymentDTAO> findAll() {
        final List<Payment> articles = paymentRepository.findAll(Sort.by("id"));
        return articles.stream()
                .map((article) -> mapToDTO(article, new PaymentDTAO()))
                .collect(Collectors.toList());
    }

    public List<Payment> fetch() {
        final List<Payment> pays = paymentRepository.findAll(Sort.by("id"));
        return pays;
    }

    public PaymentDTAO get(final Long id) {
        return paymentRepository.findById(id)
                .map(article -> mapToDTO(article, new PaymentDTAO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PaymentDTAO articleDTO) {
        final Payment article = new Payment();
        mapToEntity(articleDTO, article);
        return paymentRepository.save(article).getId();
    }

    public void update(final Long id, final PaymentDTAO actionDTO) {
        final Payment article = paymentRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(actionDTO, article);
        paymentRepository.save(article);
    }

    public void delete(final Long id) {
    	paymentRepository.deleteById(id);
    }

    public PaymentDTAO mapToDTO(final Payment payment, final PaymentDTAO paymentDTO) {
    	
    	paymentDTO.setId(payment.getId());
    	paymentDTO.setAmount(payment.getAmount());
    	paymentDTO.setBankAccount(payment.getBankAccount());
    	paymentDTO.setBankName(payment.getBankName());
    	paymentDTO.setComments(payment.getComments());
    	paymentDTO.setPaidMode(payment.getPaidMode());
    	paymentDTO.setPaymentDate(payment.getPaymentDate());
    	paymentDTO.setPaymentReference(payment.getPaymentReference());
    	paymentDTO.setTotalpaid(payment.getTotalpaid());
    	paymentDTO.setTotalvat(payment.getTotalvat());
    	paymentDTO.setDebt(payment.getDebt() == null ? null : payment.getDebt().getId());
    	
        return paymentDTO;
    }

    public Payment mapToEntity(final PaymentDTAO paymentDTO, final Payment payment) {
    	payment.setId(paymentDTO.getId());
    	
    	payment.setAmount(paymentDTO.getAmount());
    	payment.setBankAccount(paymentDTO.getBankAccount());
    	payment.setBankName(paymentDTO.getBankName());
    	payment.setComments(paymentDTO.getComments());
    	payment.setPaidMode(paymentDTO.getPaidMode());
    	payment.setPaymentDate(paymentDTO.getPaymentDate());
    	payment.setPaymentReference(paymentDTO.getPaymentReference());
    	payment.setTotalpaid(paymentDTO.getTotalpaid());
    	payment.setTotalvat(paymentDTO.getTotalvat());
    	

        final DebtRecovery debt = paymentDTO.getDebt() == null ? null : debtrepository.findById(paymentDTO.getDebt())
                .orElseThrow(() -> new NotFoundException("debts not found"));
        
        payment.setDebt(debt);
        return payment;
    }
}
