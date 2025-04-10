package com.luna.EasyInvoice.service.implementation;



import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.luna.EasyInvoice.config.EnvironmentConfig;
import com.luna.EasyInvoice.dto.DebtRecoveryDTO;
import com.luna.EasyInvoice.dto.DebtRenderDTO;
import com.luna.EasyInvoice.dto.Mouvement;
import com.luna.EasyInvoice.entities.Client;
import com.luna.EasyInvoice.entities.DebtRecovery;
import com.luna.EasyInvoice.entities.Invoice;
import com.luna.EasyInvoice.entities.Payment;
import com.luna.EasyInvoice.repository.DebtRecoveryRepository;
import com.luna.EasyInvoice.repository.InvoiceRepository;
import com.luna.EasyInvoice.service.ClientService;
import com.luna.EasyInvoice.utility.NotFoundException;
import com.luna.EasyInvoice.utility.UtilitiesService;


@Service
@Slf4j
@AllArgsConstructor
public class DebtRecoveryService {

    private final DebtRecoveryRepository debtRecoveryRepository;
    private final InvoiceRepository invoiceRepository;
    private final UtilitiesService utilitieservice;
    private final ClientService clientService;

    
    public List<DebtRecoveryDTO> findAll() {
        final List<DebtRecovery> debtRecoverys = debtRecoveryRepository.findAll(Sort.by("id"));
        return debtRecoverys.stream()
                .map((debtRecovery) -> mapToDTO(debtRecovery, new DebtRecoveryDTO()))
                .collect(Collectors.toList());
    }
    
    public List<DebtRecovery> fetch() {
        final List<DebtRecovery> debtRecoverys = debtRecoveryRepository.findAll(Sort.by("id").descending());
        return debtRecoverys;
    }

    public DebtRecoveryDTO get(final Long id) {
        return debtRecoveryRepository.findById(id)
                .map(debtRecovery -> mapToDTO(debtRecovery, new DebtRecoveryDTO()))
                .orElseThrow(NotFoundException::new);
    }
    
    public DebtRecovery getById(final Long id) {
        return debtRecoveryRepository.findById(id).get();
                
    }

    public Long create(final DebtRecoveryDTO debtRecoveryDTO) {
        final DebtRecovery debtRecovery = new DebtRecovery();
        mapToEntity(debtRecoveryDTO, debtRecovery);
        return debtRecoveryRepository.save(debtRecovery).getId();
    }

    public void update(final Long id, final DebtRecoveryDTO debtRecoveryDTO) {
        final DebtRecovery debtRecovery = debtRecoveryRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(debtRecoveryDTO, debtRecovery);
        debtRecoveryRepository.save(debtRecovery);
    }

    public void delete(final Long id) {
        debtRecoveryRepository.deleteById(id);
    }
    
    public Long recordDebt(Invoice invoice) {
    	log.info("Enregistrement de la dette");
    	DebtRecoveryDTO debt = new  DebtRecoveryDTO();
    	debt.setAmount(invoice.getSubtotal());
    	debt.setCode(UUID.randomUUID().toString().replace("-", ""));
    	debt.setCreatedBy(this.utilitieservice.getUsername());
    	//debt.setCreationDate(new Date());
    	debt.setInvoice(invoice.getId());
    	if(invoice.getPaymentMode().equals("1")) {
    		debt.setStatus(4);
    	}else {
    		debt.setStatus(1);
    	}
    	//debt.set
    	debt.setInvoiceRef(invoice.getReferenceNumber());
    	debt.setRecoveryDate(this.utilitieservice.dateAddition(new Date(), 7));
    	debt.setTotalamount(invoice.getTotalAmount());
    	debt.setType(Integer.valueOf(invoice.getPaymentMode()));
    	log.debug("Enregistrement de la dette");
    	return this.create(debt);
    }

    private DebtRecoveryDTO mapToDTO(final DebtRecovery debtRecovery,
            final DebtRecoveryDTO debtRecoveryDTO) {
        debtRecoveryDTO.setId(debtRecovery.getId());
        debtRecoveryDTO.setAmount(debtRecovery.getAmount());
        debtRecoveryDTO.setType(debtRecovery.getType());
        debtRecoveryDTO.setTotalamount(debtRecovery.getTotalamount());
        debtRecoveryDTO.setCode(debtRecovery.getCode());
        debtRecoveryDTO.setCreationDate(debtRecovery.getCreationDate());
        debtRecoveryDTO.setCreatedBy(debtRecovery.getCreatedBy());
        debtRecoveryDTO.setInvoiceRef(debtRecovery.getInvoiceRef());
        debtRecoveryDTO.setRecoveryDate(debtRecovery.getRecoveryDate());
        debtRecoveryDTO.setStatus(debtRecovery.getStatus());
        debtRecoveryDTO.setNextAppointment(debtRecovery.getNextAppointment());
        debtRecoveryDTO.setInvoice(debtRecovery.getInvoice() == null ? null : debtRecovery.getInvoice().getId());
        return debtRecoveryDTO;
    }

    private DebtRecovery mapToEntity(final DebtRecoveryDTO debtRecoveryDTO,
            final DebtRecovery debtRecovery) {
        debtRecovery.setCode(debtRecoveryDTO.getCode());
        debtRecovery.setAmount(debtRecoveryDTO.getAmount());
        debtRecovery.setType(debtRecoveryDTO.getType());
        debtRecovery.setNextAppointment(debtRecoveryDTO.getNextAppointment());
        debtRecovery.setTotalamount(debtRecoveryDTO.getTotalamount());
        debtRecovery.setCreationDate(debtRecoveryDTO.getCreationDate());
        debtRecovery.setCreatedBy(debtRecoveryDTO.getCreatedBy());
        debtRecovery.setInvoiceRef(debtRecoveryDTO.getInvoiceRef());
        debtRecovery.setRecoveryDate(debtRecoveryDTO.getRecoveryDate());
        debtRecovery.setStatus(debtRecoveryDTO.getStatus());
        final Invoice invoice = debtRecoveryDTO.getInvoice() == null ? null : invoiceRepository.findById(debtRecoveryDTO.getInvoice())
                .orElseThrow(() -> new NotFoundException("invoice not found"));
        debtRecovery.setInvoice(invoice);
        return debtRecovery;
    }

	public List<DebtRenderDTO> getStatus() {
    	List<DebtRenderDTO> debts = new ArrayList<>();
    	List<Client> clients = this.clientService.findClient();
    	if(clients!=null) {
    		if(clients.size()>0) {
    			for(Client cli : clients) {
    				List<Invoice> invoices = cli.getInvoices();
    				if(invoices!=null) {
    					if(invoices.size()>0) {
    						double paidAmount = 0.0;
    		    			double debtsAmount = 0.0;
    		    			double ratio = 0.0;
    						for(Invoice inv :invoices) {    							
    							for(DebtRecovery debt : inv.getDebts()) {
	    							if(debt!=null) {
	    								debtsAmount = debtsAmount + debt.getTotalamount();
	        							List<Payment> pyts = debt.getPayments();
	        							if(pyts!=null) {
	        								if(pyts.size()>0) {
	        									for(Payment pyt : pyts) {
	        										paidAmount = paidAmount + pyt.getTotalpaid();
	        									}
	        								}
	        							}
	    							}
								}
    						}
    						ratio = Double.compare(debtsAmount, 0)==0?45.0:paidAmount/debtsAmount * 100;
    						DebtRenderDTO deb = new DebtRenderDTO();
    						deb.setDebtAmount(debtsAmount);
    						deb.setId(cli.getId());
    						deb.setName(cli.getName());
    						deb.setPaidAmount(paidAmount);
    						deb.setRation(Math.round(ratio));
    						debts.add(deb);
    					}
    				}
    			}
    		}
    	}
    	return debts;
    }
	
	public List<Mouvement> getStatus(Client client){

    	List<Mouvement> global = new ArrayList<>();
    	for(Invoice inv : client.getInvoices()) {
			if(inv.getDebts()!=null) {
				for(DebtRecovery debt : inv.getDebts()) {
					if(debt!=null) {
						Mouvement mvt = new Mouvement();
						mvt.setCredit(debt.getTotalamount());
						mvt.setDebit(0.0);
						mvt.setLabel("FACT/INV , REF "+debt.getInvoiceRef());
						mvt.setRegistrationDate(debt.getInvoice().getInvoiceDate());
						global.add(mvt);
						
						List<Payment> pyts = debt.getPayments();
						if(pyts!=null) {
							if(pyts.size()>0) {
								for(Payment pyt : pyts) {
									Mouvement mvt1 = new Mouvement();
    								mvt1.setCredit(0.0);
    								mvt1.setDebit(pyt.getTotalpaid());
    								mvt1.setLabel("PTY , REF "+pyt.getDebt().getInvoiceRef()+"/"+pyt.getPaymentReference());
    								mvt1.setRegistrationDate(pyt.getPaymentDate());
    								global.add(mvt1);
								}
							}
						}
					}
				}
			}
		}	
    	return global;
	}

	public Object fetchUnpaid() {
		List<DebtRecovery> debtRecoverys = new ArrayList<>();
    	List<Integer> status = new ArrayList<>();
    	status.add(4);
    	if(this.utilitieservice.checkIfHasRole("ROLE_ADMIN")) {
    		debtRecoverys = debtRecoveryRepository.findByStatusNotIn(status, Sort.by("id").descending());
    	}else {
    		debtRecoverys = debtRecoveryRepository.findByStatusNotIn(status, Sort.by("id").descending());
    	}
        return debtRecoverys;
	}

}
