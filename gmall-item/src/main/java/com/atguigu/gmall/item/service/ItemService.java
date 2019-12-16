package com.atguigu.gmall.item.service;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.item.config.ItemExecutor;
import com.atguigu.gmall.item.feign.GmallPmsClient;
import com.atguigu.gmall.item.feign.GmallSmsClient;
import com.atguigu.gmall.item.feign.GmallWmsClient;
import com.atguigu.gmall.item.vo.ItemVO;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.ItemGroupVO;
import com.atguigu.gmall.vo.SaleVO;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-14 13:07
 * @version: 1.0
 * @modified By:十一。
 */
@Service
public class ItemService {

    @Autowired
    private GmallPmsClient gmallPmsClient;

    @Autowired
    private GmallWmsClient gmallWmsClient;

    @Autowired
    private GmallSmsClient gmallSmsClient;

    @Autowired
    private ThreadPoolExecutor itemExcutor;

    public ItemVO loadItem(Long skuId) throws ExecutionException, InterruptedException {
        ItemVO itemVO = new ItemVO();

        CompletableFuture<SkuInfoEntity> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            Resp<SkuInfoEntity> skuInfoEntityResp = gmallPmsClient.querySkuById(skuId);
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            Long spuId = skuInfoEntity.getSpuId();
            //sku信息
            itemVO.setSkuId(skuId);
            itemVO.setSkuTiele(skuInfoEntity.getSkuTitle());
            itemVO.setSubTitle(skuInfoEntity.getSkuSubtitle());
            itemVO.setPrice(skuInfoEntity.getPrice());
            itemVO.setWeight(skuInfoEntity.getWeight());
            return skuInfoEntity;
        },itemExcutor);

        CompletableFuture<Void> skuImageFuture = CompletableFuture.runAsync(() -> {
            Resp<List<SkuImagesEntity>> imageResp = gmallPmsClient.queryImageBySkuId(skuId);
            List<SkuImagesEntity> imagesEntities = imageResp.getData();
            if (!CollectionUtils.isEmpty(imagesEntities)) {
                itemVO.setPics(imagesEntities);       //sku图片
            }
        }, itemExcutor);

        CompletableFuture<Void> sales = CompletableFuture.runAsync(() -> {
            Resp<List<SaleVO>> resp = gmallSmsClient.queryBoundsAndFullAndLadder(skuId);
            List<SaleVO> saleVOS = resp.getData();
            itemVO.setSales(saleVOS);          //营销信息
        }, itemExcutor);

        CompletableFuture<Void> salesAttr = CompletableFuture.runAsync(() -> {
            //销售属性
            Resp<List<SkuSaleAttrValueEntity>> saleAttrsBySkuId = gmallPmsClient.querySaleAttrsBySkuId(skuId);
            List<SkuSaleAttrValueEntity> saleAttrValueEntities = saleAttrsBySkuId.getData();
            itemVO.setSaleAttrs(saleAttrValueEntities);
        }, itemExcutor);

        CompletableFuture<SkuInfoEntity> spuFuture = skuInfoFuture.thenApplyAsync(sku -> {
            //spu信息
            Resp<SpuInfoEntity> spuInfoEntityResp = gmallPmsClient.querySpuById(((SkuInfoEntity) sku).getSpuId());
            SpuInfoEntity spuInfoEntity = spuInfoEntityResp.getData();
            itemVO.setSpuId(((SkuInfoEntity) sku).getSpuId());
            itemVO.setSpuName(spuInfoEntity.getSpuName());
            return sku;
        }, itemExcutor);


        CompletableFuture<SkuInfoEntity> brandFuture = spuFuture.thenApplyAsync(sku -> {
            //品牌
            Resp<BrandEntity> brandEntityResp = gmallPmsClient.queryBrandName(((SkuInfoEntity) sku).getBrandId());
            BrandEntity brandEntity = brandEntityResp.getData();
            itemVO.setBrandEntity(brandEntity);
            return sku;
        }, itemExcutor);

        CompletableFuture<SkuInfoEntity> categoryF = spuFuture.thenApplyAsync(sku -> {
            ///分类
            Resp<CategoryEntity> categoryEntityResp = gmallPmsClient.queryCategoryName(((SkuInfoEntity) sku).getCatalogId());
            CategoryEntity categoryEntity = categoryEntityResp.getData();
            itemVO.setCategoryEntity(categoryEntity);
            return sku;
        }, itemExcutor);

        CompletableFuture<Void> storeF = CompletableFuture.runAsync(() -> {
            //库存信息
            Resp<List<WareSkuEntity>> listResp = gmallWmsClient.queryWareSkuBySkuId(skuId);
            List<WareSkuEntity> wareSkuEntities = listResp.getData();
            boolean store = wareSkuEntities.stream().anyMatch(WareSkuEntity -> WareSkuEntity.getStock() > 0);
            itemVO.setStore(store);
        }, itemExcutor);

        CompletableFuture<SkuInfoEntity> imageF = spuFuture.thenApplyAsync(sku -> {
            //描述图片
            Resp<SpuInfoDescEntity> descEntityResp = gmallPmsClient.queryImgBySpuId(((SkuInfoEntity) sku).getSpuId());
            SpuInfoDescEntity spuInfoDescEntity = descEntityResp.getData();
            if (spuInfoDescEntity != null) {
                String decript = spuInfoDescEntity.getDecript();
                String[] split = StringUtils.split(decript, ",");
                itemVO.setImages(Arrays.asList(split));

            }
            return sku;
        }, itemExcutor);

        CompletableFuture<SkuInfoEntity> groupF = spuFuture.thenApplyAsync(sku -> {
            //组（带值）
            Resp<List<ItemGroupVO>> gruopResp = this.gmallPmsClient.queryGroupByCatIdAndSpuId(((SkuInfoEntity) sku).getCatalogId(), ((SkuInfoEntity) sku).getSpuId());
            List<ItemGroupVO> groupVOS = gruopResp.getData();
            itemVO.setGroups(groupVOS);
            return sku;
        }, itemExcutor);

        CompletableFuture<Void> future = CompletableFuture.allOf(skuInfoFuture, skuImageFuture, spuFuture,sales,salesAttr,storeF
                                                                    ,brandFuture,categoryF,imageF,groupF);

        future.get();

        return itemVO;
    }
}
