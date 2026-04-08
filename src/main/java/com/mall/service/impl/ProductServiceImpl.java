package com.mall.service.impl;

import com.alibaba.fastjson.JSON;
import com.mall.po.entity.Product;
import com.mall.po.vo.ProductDoc;
import com.mall.es.ProductEsRepository;
import com.mall.mapper.ProductMapper;
import com.mall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductEsRepository esRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public Product getById(Long id) {

        String key = "product:" + id;

        String cache = redisTemplate.opsForValue().get(key);
        if(cache != null){
            return JSON.parseObject(cache, Product.class);
        }

        Product product = productMapper.selectById(id);

        if(product != null){
            redisTemplate.opsForValue().set(
                    key,
                    JSON.toJSONString(product),
                    10,
                    TimeUnit.MINUTES
            );
        }

        return product;
    }

    @Override
    public void addProduct(Product product) {
        // 1. 存MySQL
        productMapper.insert(product);

        // 2. 同步到ES（核心）
        ProductDoc doc = new ProductDoc();
        doc.setId(product.getId());
        doc.setName(product.getName());
        doc.setPrice(product.getPrice());
        doc.setStock(product.getStock());
        doc.setDescription(product.getDescription());

        esRepository.save(doc);
    }

    @Override
    public List<Product> searchByName(String name) {
        return productMapper.searchByName(name);
    }
}