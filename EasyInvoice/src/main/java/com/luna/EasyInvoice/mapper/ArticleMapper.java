package com.luna.EasyInvoice.mapper;

import com.poiji.annotation.ExcelCellName;
import com.poiji.annotation.ExcelRow;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class ArticleMapper {
	@ExcelRow                    
    private int rowIndex;
	@ExcelCellName("Title")
	private String title;
	@ExcelCellName("Description")
	private String description;
	@ExcelCellName("Price")
	private String price;
	@ExcelCellName("Tax")
	private String tax;
}
