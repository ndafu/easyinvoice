package com.luna.EasyInvoice.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.luna.EasyInvoice.pojo.SalesGeneratorPojo;
import com.luna.EasyInvoice.service.CartService;

@Service
public class CartServiceImpl implements CartService{

	@Override
	public boolean isArticleListed(List<SalesGeneratorPojo> list, SalesGeneratorPojo pojo) {
		boolean resp = false;
		if(list!=null) {
			Long id = pojo.getId();
			for(SalesGeneratorPojo po : list) {
				int retval = id.compareTo(po.getId());
				if(retval>0) {
					
				}else if(retval<0) {
					
				}else {
					resp=true;
					break;
				}
			}
		}else {
			resp=false;
		}
		return resp;
	}

	@Override
	public List<SalesGeneratorPojo> updateArticleList(List<SalesGeneratorPojo> list, SalesGeneratorPojo sales) {
		// check if exist dans list
		if(this.isArticleListed(list, sales)) {
			for(SalesGeneratorPojo li:list) {
				int rsp = sales.getId().compareTo(li.getId());
				if(rsp==0) {
					li.setQuantity(li.getQuantity()+sales.getQuantity());
					li.setTotalIncVAT(li.getTotalIncVAT()+sales.getTotalIncVAT());
					li.setTotalNVAT(li.getTotalNVAT()+sales.getTotalNVAT());
					li.setTotalVAT(li.getTotalVAT()+sales.getTotalVAT());
				}
			}
		}else {
			list.add(sales);
		}
		// if, get quantity and add it
		
		//if not, add in list
		return list;
	}

}
