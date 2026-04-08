package com.mall.controller;

import com.alibaba.excel.EasyExcel;
import com.mall.po.entity.Product;
import com.mall.po.vo.ProductDoc;
import com.mall.po.dto.ProductDto;
import com.mall.po.vo.Result;
import com.mall.es.ProductEsRepository;
import com.mall.service.ProductService;
import com.mall.util.ProductExcelListener;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductEsRepository esRepository;

    @GetMapping("/{id}")
    public Result get(@PathVariable Long id){
        return Result.success(productService.getById(id));
    }

    @PostMapping("/add")
    public Result add(@RequestBody Product product){
        productService.addProduct(product);
        return Result.success("添加成功");
    }

    @GetMapping("/search")
    public Result search(@RequestParam String name){
        // 1. 从ES中搜索
        List<ProductDoc> byNameContaining = esRepository.findByNameContaining(name);
        // 2. 从MySQL中搜索（ES中没有数据时）
        if (byNameContaining.isEmpty()) {
            List<Product> products = productService.searchByName(name);
            // 同步到ES
            List<ProductDoc> docs = products.stream().map(product -> {
                ProductDoc doc = new ProductDoc();
                BeanUtils.copyProperties(product, doc);
                return doc;
            }).collect(Collectors.toList());
            esRepository.saveAll(docs);
            byNameContaining = docs;
        }
        return Result.success(byNameContaining);
    }

    @PostMapping("/import")
    public String importExcel(@RequestParam("file") MultipartFile file) throws IOException {

        EasyExcel.read(file.getInputStream(), ProductDto.class,
                        new ProductExcelListener())
                .sheet()
                .doRead();

        return "导入成功";
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {

//        List<Product> list = productService.list();

        List<Product> list = new ArrayList<>();

        List<ProductDto> data = list.stream().map(user -> {
            ProductDto dto = new ProductDto();
            BeanUtils.copyProperties(user, dto);
            return dto;
        }).collect(Collectors.toList());

        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=users.xlsx");

        EasyExcel.write(response.getOutputStream(), ProductDto.class)
                .sheet("商品数据")
                .doWrite(data);
    }
}