package com.atguigu.gmall.search;

import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.feigin.GmallPmsClient;
import com.atguigu.gmall.search.feigin.GmallWmsClient;
import com.atguigu.gmall.search.pojo.GoodsVO;
import com.atguigu.gmall.search.pojo.SearchAttr;
import com.atguigu.gmall.search.repository.SearchRepository;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;


@SpringBootTest
class GmallSearchApplicationTests {

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    private GmallPmsClient gmallPmsClient;

    @Autowired
    private GmallWmsClient gmallWmsClient;

    @Autowired
    SearchRepository searchRepository;

    @Test
    void contextLoads() {
        restTemplate.createIndex(GoodsVO.class);
        restTemplate.putMapping(GoodsVO.class);
    }

    @Test
    void addData(){

        Long pageNum = 1l;
        Long pageSize = 100l;

        do {
            //查询spu
            QueryCondition condition = new QueryCondition();
            condition.setPage(pageNum);
            condition.setLimit(pageSize);
            Resp<List<SpuInfoEntity>> spusResp = gmallPmsClient.querySpuInfo(condition);
            List<SpuInfoEntity> spus = spusResp.getData();
            if (CollectionUtils.isEmpty(spus)){
                continue;
            }
            //查询sku
            spus.forEach(spuInfoEntity -> {
                Resp<List<SkuInfoEntity>> skusResp = gmallPmsClient.querySkusBySpuId(spuInfoEntity.getId());
                List<SkuInfoEntity> skus = skusResp.getData();
                if (!CollectionUtils.isEmpty(skus)){
                    List<GoodsVO> goodsVOS = skus.stream().map(skuInfoEntity -> {
                        GoodsVO goodsVO = new GoodsVO();
                        goodsVO.setSkuId(skuInfoEntity.getSkuId());
                        goodsVO.setPic(skuInfoEntity.getSkuDefaultImg());
                        goodsVO.setTitle(skuInfoEntity.getSkuTitle());
                        goodsVO.setPrice(skuInfoEntity.getPrice().doubleValue());
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
            });
            pageSize = (long)spus.size();
            pageNum ++ ;
        }while (pageSize == 100);


    }

}
