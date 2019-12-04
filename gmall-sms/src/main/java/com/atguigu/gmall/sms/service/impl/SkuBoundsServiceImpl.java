package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.service.SkuFullReductionService;
import com.atguigu.gmall.sms.service.SkuLadderService;
import com.atguigu.gmall.vo.SkuSmsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.sms.dao.SkuBoundsDao;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.service.SkuBoundsService;


@Service("skuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsDao, SkuBoundsEntity> implements SkuBoundsService {

    @Autowired
    private SkuFullReductionService skuFullReductionService;

    @Autowired
    private SkuLadderService skuLadderService;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuBoundsEntity> page = this.page(
                new Query<SkuBoundsEntity>().getPage(params),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public void saveSms(SkuSmsVO skuSmsVO) {
        // 3.1 积分信息保存
        SkuBoundsEntity boundsEntity = new SkuBoundsEntity();
        boundsEntity.setSkuId(skuSmsVO.getSkuId());
        boundsEntity.setBuyBounds(skuSmsVO.getBuyBounds());
        boundsEntity.setGrowBounds(skuSmsVO.getGrowBounds());
        List<Integer> voWork = skuSmsVO.getWork();
        boundsEntity.setWork(voWork.get(0) * 8 + voWork.get(1) * 4 + voWork.get(2) * 2 + voWork.get(3) * 1);
        // 保存到数据库
        this.save(boundsEntity);

        // 3.2满减信息保存
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        skuFullReductionEntity.setSkuId(skuSmsVO.getSkuId());
        skuFullReductionEntity.setFullPrice(skuSmsVO.getFullPrice());
        skuFullReductionEntity.setReducePrice(skuSmsVO.getReducePrice());
        skuFullReductionEntity.setAddOther(skuSmsVO.getFullAddOther());
        // 保存到数据库
        skuFullReductionService.save(skuFullReductionEntity);

        // 3.3打折信息保存
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuSmsVO.getSkuId());
        skuLadderEntity.setPrice(skuSmsVO.getPrice());
        skuLadderEntity.setFullCount(skuSmsVO.getFullCount());
        skuLadderEntity.setDiscount(skuSmsVO.getDiscount());
        skuLadderEntity.setAddOther(skuSmsVO.getLadderAddOther());
        // 保存到数据库
        skuLadderService.save(skuLadderEntity);

    }

}