package com.mall.util;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.mall.entity.Product;
import com.mall.entity.ProductDto;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductExcelListener extends AnalysisEventListener<ProductDto> {

    private static final int BATCH_COUNT = 1000;

    private List<Product> cacheList  = new ArrayList<>();

    @Override
    public void invoke(ProductDto data, AnalysisContext analysisContext) {
        // DTO → 实体
        Product product = new Product();
        BeanUtils.copyProperties(data, product);

        cacheList.add(product);

        // 批量入库
        if (cacheList.size() >= BATCH_COUNT) {
//            userService.saveBatch(cacheList);
            cacheList.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if (!cacheList.isEmpty()) {
//            userService.saveBatch(cacheList);
        }
    }
}
