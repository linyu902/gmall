package com.atguigu.gmall.pms.api;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.CategoryVO;
import com.atguigu.gmall.pms.vo.ItemGroupVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("pms/spuinfo/info/{id}")
    public Resp<SpuInfoEntity> querySpuById(@PathVariable("id") Long id);

    @GetMapping("pms/spuinfodesc/info/{spuId}")
    public Resp<SpuInfoDescEntity> queryImgBySpuId(@PathVariable("spuId") Long spuId);

    @GetMapping("pms/attrgroup/item/group/{catId}/{spuId}")
    public Resp<List<ItemGroupVO>> queryGroupByCatIdAndSpuId(@PathVariable("catId") Long catId, @PathVariable("spuId") Long spuId);

    /**
     * 通过spuId查询sku
     * @param spuId
     * @return
     */
    @GetMapping("pms/skuinfo/{spuId}")
    public Resp<List<SkuInfoEntity>> querySkusBySpuId(@PathVariable("spuId") Long spuId);

    @GetMapping("pms/skuinfo/info/{skuId}")
    public Resp<SkuInfoEntity> querySkuById(@PathVariable("skuId") Long skuId);

    @GetMapping("/pms/skuimages/{skuId}")
    public Resp<List<SkuImagesEntity>> queryImageBySkuId(@PathVariable("skuId") Long skuId);

    @GetMapping("pms/skusaleattrvalue/{skuId}")
    public Resp<List<SkuSaleAttrValueEntity>> querySaleAttrsBySkuId(@PathVariable("skuId") Long skuId);

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

    @GetMapping("pms/category")
    public Resp<List<CategoryEntity>> queryCategoryEntites(@RequestParam(value = "level" , defaultValue = "0")Integer level,
                                                           @RequestParam(value="parentCid", required = false)Long parentCid);

    @GetMapping("pms/category/{pid}")
    public Resp<List<CategoryVO>> querySubCategoryEntites(@PathVariable("pid") Long pid);
}
