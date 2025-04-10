package com.luna.EasyInvoice.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UtilityController {
	@GetMapping("/getcode")
	public @ResponseBody Response getwarehousecode() {
		Response rsp = new Response();
		UUID.randomUUID();
		String code = UUID.randomUUID().toString();
		if(code.isEmpty()) {
			rsp.setCode(0);
			rsp.setDescription("Impossible to generate the code");
		}else {
			rsp.setCode(1);
			rsp.setDescription("success");
			List<Object> obj = new ArrayList<>();
			obj.add(code);
			rsp.setBody(obj);
		}
		return rsp;
	}
}
