package com.example.query_builder.service;

import com.example.query_builder.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ETYRED
 * @since 2022-06-29
 */
public interface ProductService extends IService<Product> {

    String parseQueryRules(String queryRules);
}
