package com.mall.service;

import com.mall.po.entity.Product;

import java.util.List;
import java.util.Map;

public interface ProductService {
    Product getById(Long id);
    void addProduct(Product product);

    List<Product> searchByName(String name);

    Map<String, Object> searchByNameWithPage(String name, Integer pageNum, Integer pageSize);
}