package com.mall.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Product {
    private Long id;
    private String name;
    private Integer stock;
    private BigDecimal price;
    private String description;
}