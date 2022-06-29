package com.example.query_builder.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.query_builder.entity.Product;
import com.example.query_builder.mapper.ProductMapper;
import com.example.query_builder.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.velocity.runtime.directive.Foreach;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ETYRED
 * @since 2022-06-29
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public String parseQueryRules(String queryRules) {
        StringBuffer sql = new StringBuffer();
        JSONObject group = JSONObject.parseObject(queryRules);
        String result = sql.append(recursion(group)).toString();
        return result;
    }

    public String recursion(JSONObject group){
        StringBuffer sql = new StringBuffer();
        boolean isRootRule = group.containsKey("essential");
        sql.append(isRootRule ? "where " : "(");
        int rulesCount = 0;
        JSONArray rules = JSON.parseArray(group.getString("rules"));
        for(Object obj : rules){
            JSONObject rule = (JSONObject) obj;
            if(rule.containsKey("condition")){
                if (++rulesCount > 1){
                    sql.append(" " + group.getString("condition") + " ");
                }
                sql.append(recursion(rule));
            }else{
                if (++rulesCount > 1){
                    sql.append(" " + group.getString("condition") + " ");
                }
                sql.append(rule.getString("field")+castOperation(rule));

            }
        }
        sql.append(isRootRule ? "" : ")");
        return sql.toString();
    }

    private String castOperation(JSONObject rule) {
        StringBuffer sql = new StringBuffer();
        String str = rule.getString("string");
        String value1 = rule.getString("value1");
        String value2 = rule.getString("value2");
        String operation = rule.getString("operation");
        switch (operation){
            default:{
                sql.append("error");
                break;
            }
            case "equal":{
                if ("price".equals(rule.getString("field"))){
                    sql.append(" = "+str);
                }else {
                    sql.append(" = '"+str+"'");
                }
                break;
            }
            case "not equal":{
                if ("price".equals(rule.getString("field"))){
                    sql.append(" != "+str);
                }else {
                    sql.append(" != '"+str+"'");
                }
                break;
            }
            case "less":{
                sql.append(" < "+str);
                break;
            }
            case "greater":{
                sql.append(" > "+str);
                break;
            }
            case "less or equal":{
                sql.append(" <= "+str);
                break;
            }
            case "equal or greater":{
                sql.append(" >= "+str);
                break;
            }
            case "between":{
                sql.append(" between "+value1+" and "+value2);
                break;
            }
            case "not between":{
                sql.append(" not between "+value1+" and "+value2);
                break;
            }
        }
        return sql.toString();
    }
}
