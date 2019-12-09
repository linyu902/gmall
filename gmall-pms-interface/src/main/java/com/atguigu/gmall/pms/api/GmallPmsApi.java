package com.atguigu.gmall.pms.api;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
    @GetMapping("pms/spuinfo/list")
    public Resp<PageVo> list(QueryCondition queryCondition);

    /**
     * 通过spuId查询sku
     * @param spuId
     * @return
     */
    @GetMapping("pms/skuinfo/{spuId}")
    public Resp<List<SkuInfoEntity>> querySkusBySpuId(@PathVariable Long spuId);

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
    public Resp<List<ProductAttrValueEntity>> querySearchAttrBySpuId(@PathVariable Long spuId);
}
