package com.luna.EasyInvoice.pojo;

import lombok.Data;

@Data
public class StatisticPojo {
	private String key;
	private double value;
	public StatisticPojo(String key, double value) {
		this.key = key;
		this.value = value;
	}
}
