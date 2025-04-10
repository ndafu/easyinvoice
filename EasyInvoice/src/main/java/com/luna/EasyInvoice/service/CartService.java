package com.luna.EasyInvoice.service;

import java.util.List;

import com.luna.EasyInvoice.pojo.SalesGeneratorPojo;

public interface CartService {
	boolean isArticleListed(List<SalesGeneratorPojo> list, SalesGeneratorPojo pojo);
	List<SalesGeneratorPojo> updateArticleList(List<SalesGeneratorPojo> list, SalesGeneratorPojo sales);
}
