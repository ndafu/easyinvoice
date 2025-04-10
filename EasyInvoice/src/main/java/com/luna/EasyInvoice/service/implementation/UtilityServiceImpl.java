package com.luna.EasyInvoice.service.implementation;

import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;

import com.luna.EasyInvoice.service.UtilityService;

@Service
public class UtilityServiceImpl implements UtilityService {

	@Override
	public int getRandomNumber(int start, int end) {
		return ThreadLocalRandom.current().nextInt(start,end);
	}

}
