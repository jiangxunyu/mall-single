package com.mall.controller;

import com.mall.po.vo.ProductDoc;
import com.mall.po.vo.Result;
import com.mall.es.EsSearchService;
import com.mall.es.ProductEsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/es")
public class EsController {

    @Autowired
    private ProductEsRepository repository;
    @Autowired
    private EsSearchService esSearchService;

    @GetMapping("/searchByName")
    public Result searchByName(@RequestParam String keyword){
        return Result.success(repository.findByNameContaining(keyword));
    }

    @GetMapping("/search")
    public Result search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<ProductDoc> search = esSearchService.search(keyword, page, size);
        List<ProductDoc> content = search.getContent();
        return Result.success(content);
    }
}
