package com.mall.es;

import com.mall.entity.Product;
import com.mall.entity.ProductDoc;
import com.mall.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductSyncService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductEsRepository esRepository;

    public void syncAll(){

        List<Product> list = productMapper.selectAll();

        List<ProductDoc> docs = list.stream().map(p -> {
            ProductDoc doc = new ProductDoc();
            doc.setId(p.getId());
            doc.setName(p.getName());
            doc.setDescription(p.getName());
            doc.setPrice(p.getPrice());
            return doc;
        }).collect(Collectors.toList());

        esRepository.saveAll(docs);
    }
}