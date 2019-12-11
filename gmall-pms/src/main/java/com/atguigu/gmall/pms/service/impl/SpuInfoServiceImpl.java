package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feign.SmsSkuClient;
import com.atguigu.gmall.pms.service.*;
import com.atguigu.gmall.pms.vo.BaseAttrVO;
import com.atguigu.gmall.pms.vo.SkuInfoVO;
import com.atguigu.gmall.pms.vo.SpuInfoVO;
import com.atguigu.gmall.vo.SkuSmsVO;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService descService;

    @Autowired
    private ProductAttrValueService attrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private AttrDao attrDao;

    @Autowired
    private SmsSkuClient smsSkuClient;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Value("${item.rabbitmq.exchangeName}")
    private String EXCHANGE_NAME;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo querySpuInfo(QueryCondition condition, Long catId) {

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        if (catId != 0){
            wrapper.eq("catalog_id",catId);
        }

        String key = condition.getKey();
        if (StringUtils.isNoneBlank(key)){
            wrapper.and(t -> t.like("id",key))
            .or().like("spu_name",key);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(condition),
                wrapper
        );

        return new PageVo(page);
    }

    @Override
    @Transactional
    public void bigSave(SpuInfoVO spuInfoVO) {

        // 1.保存pms_spu_info信息   时间需要设置
        // 1.1保存主要信息
        Long spuId = saveSpuInfo(spuInfoVO);
//        System.out.println("spuInfoVO = " + spuInfoVO);
        // 1.2保存图片信息
        this.descService.saveSpuInfoDesc(spuInfoVO, spuId);
        // 1.3保存基本属性信息
        saveBaseAttrValue(spuInfoVO, spuId);

        // 2.保存pms_sku_info
        //2.1 保存sku信息
        saveSkuAndSale(spuInfoVO, spuId);
        this.sendMsg("insert",spuId);
    }

    private void sendMsg(String type , long spuId){

        amqpTemplate.convertAndSend(EXCHANGE_NAME,"item." + type ,spuId);
    }

    private void saveSkuAndSale(SpuInfoVO spuInfoVO, Long spuId) {
        List<SkuInfoVO> skus = spuInfoVO.getSkus();
        if (! CollectionUtils.isEmpty(skus)){
            skus.forEach(skuInfoVO -> {
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(skuInfoVO,skuInfoEntity);
                //设置spuId,skucode,catalog_id,brand_id
                skuInfoEntity.setSpuId(spuId);
                skuInfoEntity.setSkuCode(UUID.randomUUID().toString().substring(0,8));
                skuInfoEntity.setCatalogId(spuInfoVO.getCatalogId());
                skuInfoEntity.setBrandId(spuInfoVO.getBrandId());
                //设置默认图片
                List<String> skuImages = skuInfoVO.getImages();
                if (! CollectionUtils.isEmpty(skuImages)){
                    skuInfoEntity.setSkuDefaultImg(skuInfoEntity.getSkuDefaultImg() == null ?
                            skuImages.get(0) : skuInfoEntity.getSkuDefaultImg());
                }
                //保存到数据库
                skuInfoService.save(skuInfoEntity);
                //获取skuId
                Long skuId = skuInfoEntity.getSkuId();

                //2.1 保存图片
                skuImages.forEach(skuImage -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setImgUrl(skuImage);
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setDefaultImg(StringUtils.equals(skuImage,skuInfoEntity.getSkuDefaultImg()) ?
                            1 : 0);
                    //保存到数据库
                    skuImagesService.save(skuImagesEntity);
                });

                //2.2 保存销售属性
                List<SkuSaleAttrValueEntity> saleAttrs = skuInfoVO.getSaleAttrs();
                saleAttrs.forEach(skuSaleAttrValueEntity -> {
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    skuSaleAttrValueEntity.setAttrName(attrDao.selectById(skuSaleAttrValueEntity.getAttrId()).getAttrName());
                    skuSaleAttrValueService.save(skuSaleAttrValueEntity);
                });

                //3. 保存积分，优惠信息
                System.out.println("-------------------->" );
                SkuSmsVO skuSmsVO = new SkuSmsVO();
                BeanUtils.copyProperties(skuInfoVO,skuSmsVO);
                skuSmsVO.setSkuId(skuId);
                smsSkuClient.saveSms(skuSmsVO);

            });
        }
    }

    private void saveBaseAttrValue(SpuInfoVO spuInfoVO, Long spuId) {
        List<BaseAttrVO> baseAttrs = spuInfoVO.getBaseAttrs();
        if (! CollectionUtils.isEmpty(baseAttrs)){
            baseAttrs.forEach(baseAttrVO -> {
                baseAttrVO.setSpuId(spuId);
                attrValueService.save(baseAttrVO);
            });
        }
    }

    private Long saveSpuInfo(SpuInfoVO spuInfoVO) {
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfoVO,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUodateTime(spuInfoEntity.getCreateTime());
        this.save(spuInfoEntity);
        return spuInfoEntity.getId();
    }

}