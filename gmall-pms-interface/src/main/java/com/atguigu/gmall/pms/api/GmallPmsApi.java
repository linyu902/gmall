package com.atguigu.gmall.pms.api;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-09 18:43
 * @version: 1.0
 * @modified By:十一。
 */
public interface GmallPmsApi {
    /**
     * 查询spu
     * @param queryCondition
     * @return
     */
    @PostMapping("pms/spuinfo/page")
    public Resp<List<SpuInfoEntity>> querySpuInfo(@RequestBody QueryCondition queryCondition);

    /**
     * 通过spuId查询sku
     * @param spuId
     * @return
     */
    @GetMapping("pms/skuinfo/{spuId}")
    public Resp<List<SkuInfoEntity>> querySkusBySpuId(@PathVariable("spuId") Long spuId);

    /**
     * 根据品牌Id查询品牌信息
     * @param brandId
     * @return
     */
    @GetMapping("pms/brand/info/{brandId}")
    public Resp<BrandEntity> queryBrandName(@PathVariable("brandId") Long brandId);

    /**
     * 根据分类Id查询分类信息
     * @param catId
     * @return
     */
    @GetMapping("pms/category/info/{catId}")
    public Resp<CategoryEntity> queryCategoryName(@PathVariable("catId") Long catId);

    /**
     * pms_attr与pms_product_attr_value两表联查，获取搜索属性
     * @param spuId
     * @return
     */
    @GetMapping("pms/productattrvalue/{spuId}")
    public Resp<List<ProductAttrValueEntity>> querySearchAttrBySpuId(@PathVariable("spuId") Long spuId);
}
