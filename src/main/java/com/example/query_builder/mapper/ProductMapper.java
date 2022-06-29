package com.example.query_builder.mapper;

import com.example.query_builder.entity.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ETYRED
 * @since 2022-06-29
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

}
