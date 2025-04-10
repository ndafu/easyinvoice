package com.luna.EasyInvoice.service.implementation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.luna.EasyInvoice.dto.ActionDTO;
import com.luna.EasyInvoice.entities.Action;
import com.luna.EasyInvoice.entities.DebtRecovery;
import com.luna.EasyInvoice.repository.ActionRepository;
import com.luna.EasyInvoice.repository.DebtRecoveryRepository;
import com.luna.EasyInvoice.utility.NotFoundException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ActionService {

	final private ActionRepository actionrepository;
	final private DebtRecoveryRepository debtrepository;
	
    
//    public List<ActionDTO> findAll(List<Long> ids){
//    	 final List<Action> articles = actionrepository.findByIdNotIn(ids);
//         return articles.stream()
//                 .map((article) -> mapToDTO(article, new ArticleDTO()))
//                 .collect(Collectors.toList());
//    }
    
    

    public List<ActionDTO> findAll() {
        final List<Action> articles = actionrepository.findAll(Sort.by("id"));
        return articles.stream()
                .map((article) -> mapToDTO(article, new ActionDTO()))
                .collect(Collectors.toList());
    }

    public List<Action> fetch() {
        final List<Action> articles = actionrepository.findAll(Sort.by("id").descending());
        return articles;
    }
    public ActionDTO get(final Long id) {
        return actionrepository.findById(id)
                .map(article -> mapToDTO(article, new ActionDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ActionDTO articleDTO) {
        final Action article = new Action();
        mapToEntity(articleDTO, article);
        return actionrepository.save(article).getId();
    }

    public void update(final Long id, final ActionDTO actionDTO) {
        final Action article = actionrepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(actionDTO, article);
        actionrepository.save(article);
    }

    public void delete(final Long id) {
        actionrepository.deleteById(id);
    }

    public ActionDTO mapToDTO(final Action action, final ActionDTO actionDTO) {
    	actionDTO.setId(action.getId());
    	actionDTO.setActionDate(action.getActionDate());
    	actionDTO.setComments(action.getComments());
    	actionDTO.setContactedEmail(action.getContactedEmail());
    	actionDTO.setContactedPerson(action.getContactedPerson());
    	actionDTO.setContactedTelephone(action.getContactedTelephone());
    	actionDTO.setCreatedBy(action.getCreatedBy());
    	actionDTO.setNextAppointment(action.getNextAppointment());
    	actionDTO.setFnalDecision(action.getFnalDecision());
    	actionDTO.setId(action.getId());
    	actionDTO.setNextStep(action.getNextStep());
    	actionDTO.setResult(action.getResult());
    	actionDTO.setType(action.getType());
    	actionDTO.setUsedMean(action.getUsedMean());
    	actionDTO.setWhoExecuted(action.getWhoExecuted());
    	actionDTO.setDebt(action.getDebt() == null ? null : action.getDebt().getId());
    	//.setDebt(null);
    	
        return actionDTO;
    }

    public Action mapToEntity(final ActionDTO actionDTO, final Action action) {
    	action.setId(actionDTO.getId());
    	
    	action.setId(actionDTO.getId());
    	action.setActionDate(actionDTO.getActionDate());
    	action.setComments(actionDTO.getComments());
    	action.setContactedEmail(actionDTO.getContactedEmail());
    	action.setContactedPerson(actionDTO.getContactedPerson());
    	action.setContactedTelephone(actionDTO.getContactedTelephone());
    	action.setCreatedBy(actionDTO.getCreatedBy());
    	action.setFnalDecision(actionDTO.getFnalDecision());
    	action.setNextAppointment(actionDTO.getNextAppointment());
    	action.setId(actionDTO.getId());
    	action.setNextStep(actionDTO.getNextStep());
    	action.setResult(actionDTO.getResult());
    	action.setType(actionDTO.getType());
    	action.setUsedMean(actionDTO.getUsedMean());
    	action.setWhoExecuted(actionDTO.getWhoExecuted());

        final DebtRecovery debt = actionDTO.getDebt() == null ? null : debtrepository.findById(actionDTO.getDebt())
                .orElseThrow(() -> new NotFoundException("debts not found"));
        
        action.setDebt(debt);
        return action;
    }
}
