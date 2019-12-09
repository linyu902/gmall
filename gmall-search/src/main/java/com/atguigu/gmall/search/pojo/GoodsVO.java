package com.atguigu.gmall.search.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-09 16:21
 * @version: 1.0
 * @modified By:十一。
 */
@Data
@Document(indexName = "goods",type = "info" ,shards = 3, replicas = 2)
public class GoodsVO {

    @Id
    private Long skuId;        //skuId

    @Field(type = FieldType.Keyword,index = false)
    private String pic;     //sku的默认图片

    @Field(type = FieldType.Double)
    private Double price;   //sku的价格

    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String title;    //sku标题

    @Field(type = FieldType.Integer)
    private Integer sale;   //销量

    @Field(type = FieldType.Long)
    private Long store;     //是否有货

    @Field(type = FieldType.Long)
    private Long brandId;   //品牌Id

    @Field(type = FieldType.Keyword)
    private String brandName;   //品牌名

    @Field(type = FieldType.Long)
    private Long categoryId;    //分类Id

    @Field(type = FieldType.Keyword)
    private String categoryName;    //分类名

    @Field(type = FieldType.Date)
    private Date createTime;       //创建时间

    @Field(type = FieldType.Nested)
    private List<SearchAttr> attrs; //检索属性
}
