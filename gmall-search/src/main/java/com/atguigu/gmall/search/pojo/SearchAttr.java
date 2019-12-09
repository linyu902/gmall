package com.atguigu.gmall.search.pojo;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-09 18:08
 * @version: 1.0
 * @modified By:十一。
 */
@Data
public class SearchAttr {

    @Field(type = FieldType.Long)
    private Long attrId;

    @Field(type = FieldType.Keyword)
    private String attrName;

    @Field(type = FieldType.Keyword)
    private String attrValue;

}
