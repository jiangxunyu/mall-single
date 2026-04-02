package com.mall.service;

import com.mall.entity.Product;

import java.util.List;

public interface ProductService {
    Product getById(Long id);
    void addProduct(Product product);

    List<Product> searchByName(String name);
}