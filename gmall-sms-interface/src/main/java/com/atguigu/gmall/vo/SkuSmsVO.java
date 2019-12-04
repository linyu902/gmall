package com.atguigu.gmall.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-04 18:51
 * @version: 1.0
 * @modified By:十一。
 */
@Data
public class SkuSmsVO {

    private Long skuId;
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
