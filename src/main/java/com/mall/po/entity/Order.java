package com.mall.po.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    private Long id;
    private Long userId;
    private Integer status;
    private Date createTime;
    private BigDecimal totalAmount;
}