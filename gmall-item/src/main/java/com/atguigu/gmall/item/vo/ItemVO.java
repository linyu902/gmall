package com.atguigu.gmall.item.vo;

import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.pms.vo.ItemGroupVO;
import com.atguigu.gmall.vo.SaleVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-14 13:05
 * @version: 1.0
 * @modified By:十一。
 */
@Data
public class ItemVO {

    private Long skuId;
    private CategoryEntity categoryEntity;
    private BrandEntity brandEntity;
    private Long spuId;
    private String spuName;

    private String skuTiele;
    private String subTitle;
    private BigDecimal price;
    private BigDecimal weight;

    private List<SkuImagesEntity> pics;     //sku图片信息
    private List<SaleVO> sales;          //营销信息

    private Boolean store;                  //是否有货

    private List<SkuSaleAttrValueEntity> saleAttrs;     //销售信息

    private List<String> images;            //spu海报

    private List<ItemGroupVO> groups;       //规格参数组及组下参数


}
