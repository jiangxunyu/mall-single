package com.mall.po.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {

    @ExcelProperty("id")
    private Long id;
    @ExcelProperty("姓名")
    private String name;
    @ExcelProperty("库存")
    private Integer stock;
    @ExcelProperty("价格")
    private BigDecimal price;
    @ExcelProperty("描述")
    private String description;
}
