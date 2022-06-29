package com.example.query_builder.controller;


import com.example.query_builder.entity.Product;
import com.example.query_builder.service.ProductService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ETYRED
 * @since 2022-06-29
 */
@RestController
@CrossOrigin
@RequestMapping("/product")
public class ProductController {

    @Resource
    ProductService productService;

    @PostMapping("submitQueryRules")
    public String submitQueryRules(@RequestParam String queryRules){
        System.out.println(queryRules);
        String result = productService.parseQueryRules(queryRules);
        System.out.println(result);
        return result;
    }
}

