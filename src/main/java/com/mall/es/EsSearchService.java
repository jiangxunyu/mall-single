package com.mall.es;

import com.mall.entity.ProductDoc;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface EsSearchService {
    Page<ProductDoc> search(String keyword, Integer page, Integer size);
}