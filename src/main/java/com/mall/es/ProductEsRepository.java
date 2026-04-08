package com.mall.es;

import com.mall.po.vo.ProductDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductEsRepository extends ElasticsearchRepository<ProductDoc, Long> {

    List<ProductDoc> findByNameContaining(String keyword);
}