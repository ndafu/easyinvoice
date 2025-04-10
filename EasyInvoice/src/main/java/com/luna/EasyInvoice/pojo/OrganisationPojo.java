package com.luna.EasyInvoice.pojo;

import java.util.List;

import com.luna.EasyInvoice.entities.Organisation;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrganisationPojo extends Organisation{
	private List<InterconnectionPojo> intercons;
	
	
}

