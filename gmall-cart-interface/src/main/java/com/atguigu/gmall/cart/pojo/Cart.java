package com.atguigu.gmall.cart.pojo;

import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.vo.SaleVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-17 15:20
 * @version: 1.0
 * @modified By:十一。
 */
@Data
public class Cart {

    private Long skuId;

    private String title;

    private String defaultImage;

    private BigDecimal price;

    private BigDecimal currentPrice;

    private Integer count;

    private Boolean check;

    private Boolean store;

    private List<SaleVO> saleVOS;

    private List<SkuSaleAttrValueEntity> saleAttrs;
}
