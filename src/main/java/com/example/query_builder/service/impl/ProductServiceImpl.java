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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

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
        StringBuffer sql = new StringBuffer("select id,name,category,price from product ");
        JSONObject group = JSONObject.parseObject(queryRules);
        sql.append(recursion(group)).toString();
        try {
            //加载驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            //数据库地址，本机、端口号3306、数据库名为test
            String url = "jdbc:mysql://localhost:3306/query_builder?serverTimezone=GMT%2B8&characterEncoding=utf-8";
            //用户名
            String user = "root";
            //密码
            String pwd = "123456";
            //连接数据库
            Connection conn = DriverManager.getConnection(url, user, pwd);
            //创建Statement对象
            Statement stmt = conn.createStatement();
            //执行SQL语句
            ResultSet rs = stmt.executeQuery(sql.toString());
            JSONArray jsonArray = new JSONArray();
            while (rs.next()){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id",rs.getString("id"));
                jsonObject.put("name",rs.getString("name"));
                jsonObject.put("category",rs.getString("category"));
                jsonObject.put("price",rs.getString("price"));
                jsonArray.add(jsonObject);
            }
            sql.append("\n\n查询结果:\n");
            sql.append(jsonArray);
            System.out.println(jsonArray);
        }catch(Exception e){
            e.printStackTrace();
        }
        return sql.toString();
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
