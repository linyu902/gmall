package com.atguigu.gmall.search.listener;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.feigin.GmallPmsClient;
import com.atguigu.gmall.search.feigin.GmallWmsClient;
import com.atguigu.gmall.search.pojo.GoodsVO;
import com.atguigu.gmall.search.pojo.SearchAttr;
import com.atguigu.gmall.search.repository.SearchRepository;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-11 16:30
 * @version: 1.0
 * @modified By:十一。
 */
@Component
public class SpuInfoListener {

    @Autowired
    private GmallPmsClient gmallPmsClient;

    @Autowired
    private GmallWmsClient gmallWmsClient;

    @Autowired
    SearchRepository searchRepository;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "gmall.item.insert",declare = "true"),   //队列名，是否持久化
                    exchange = @Exchange(value = "GMALL-PMS-EXCHANGE",              //交换机名
                                        ignoreDeclarationExceptions = "true",       //忽略
                                        type = ExchangeTypes.TOPIC),                //队列类型
                    key = {"item.insert","item.update"}                             //能接收的key
            )
    )
    public void listenInsert(Long spuId){
        Resp<List<SkuInfoEntity>> skusResp = gmallPmsClient.querySkusBySpuId(spuId);
        List<SkuInfoEntity> skus = skusResp.getData();
        if (!CollectionUtils.isEmpty(skus)){
            List<GoodsVO> goodsVOS = skus.stream().map(skuInfoEntity -> {
                GoodsVO goodsVO = new GoodsVO();
                goodsVO.setSkuId(skuInfoEntity.getSkuId());
                goodsVO.setPic(skuInfoEntity.getSkuDefaultImg());
                goodsVO.setTitle(skuInfoEntity.getSkuTitle());
                goodsVO.setPrice(skuInfoEntity.getPrice().doubleValue());
                SpuInfoEntity spuInfoEntity = gmallPmsClient.querySpuById(spuId).getData();
                goodsVO.setCreateTime(spuInfoEntity.getCreateTime());
                //品牌Id
                goodsVO.setBrandId(skuInfoEntity.getBrandId());
                Resp<BrandEntity> brandResp = gmallPmsClient.queryBrandName(skuInfoEntity.getBrandId());
                BrandEntity brandEntity = brandResp.getData();
                //品牌名
                if (brandEntity != null) {
                    goodsVO.setBrandName(brandEntity.getName());
                }
                //分类Id
                goodsVO.setCategoryId(skuInfoEntity.getCatalogId());
                CategoryEntity categoryEntity = gmallPmsClient.queryCategoryName(skuInfoEntity.getCatalogId()).getData();
                if (categoryEntity != null) {
                    goodsVO.setCategoryName(categoryEntity.getName());
                }
                //库存
                Resp<List<WareSkuEntity>> wareResp = gmallWmsClient.queryWareSkuBySkuId(skuInfoEntity.getSkuId());
                List<WareSkuEntity> wareSkuEntities = wareResp.getData();
                if (CollectionUtils.isEmpty(wareSkuEntities)){
                    boolean flag = wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0);
                    goodsVO.setStore(flag);
                }
                //销量
                goodsVO.setSale(0);
                //搜索属性
                Resp<List<ProductAttrValueEntity>> attrResp = gmallPmsClient.querySearchAttrBySpuId(spuInfoEntity.getId());
                List<ProductAttrValueEntity> productAttrValueEntities = attrResp.getData();
                if (!CollectionUtils.isEmpty(productAttrValueEntities)) {
                    List<SearchAttr> attrs = productAttrValueEntities.stream().map(productAttrValueEntity -> {
                        SearchAttr searchAttr = new SearchAttr();
                        searchAttr.setAttrId(productAttrValueEntity.getAttrId());
                        searchAttr.setAttrName(productAttrValueEntity.getAttrName());
                        searchAttr.setAttrValue(productAttrValueEntity.getAttrValue());
                        return searchAttr;
                    }).collect(Collectors.toList());
                    goodsVO.setAttrs(attrs);
                }
                return goodsVO;
            }).collect(Collectors.toList());

            searchRepository.saveAll(goodsVOS);
        }
    }
}
