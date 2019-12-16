package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-14 13:19
 * @version: 1.0
 * @modified By:十一。
 */
@Data
public class ItemGroupVO {

    private String name;

    private List<ProductAttrValueEntity> baseAttrs;
}
