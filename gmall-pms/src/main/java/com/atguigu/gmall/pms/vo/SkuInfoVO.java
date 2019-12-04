package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-04 15:11
 * @version: 1.0
 * @modified By:十一。
 */
@Data
public class SkuInfoVO extends SkuInfoEntity {

    private List<String> images;

    private List<SkuSaleAttrValueEntity> saleAttrs;

    // 积分相关
    private BigDecimal growBounds;
    private BigDecimal buyBounds;
    private List<Integer> work;

    // 打折相关
    private Integer fullCount;
    private BigDecimal discount;
    private BigDecimal price;
    private Integer ladderAddOther;

    // 满减相关
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer fullAddOther;
}
