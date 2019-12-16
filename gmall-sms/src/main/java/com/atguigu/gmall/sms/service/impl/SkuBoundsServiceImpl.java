package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.service.SkuFullReductionService;
import com.atguigu.gmall.sms.service.SkuLadderService;
import com.atguigu.gmall.vo.SaleVO;
import com.atguigu.gmall.vo.SkuSmsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Override
    public List<SaleVO> queryBoundsAndFullAndLadder(Long skuId) {
        ArrayList<SaleVO> saleVOS = new ArrayList<>();
        //积分
        SkuBoundsEntity boundsEntity = this.getOne(new QueryWrapper<SkuBoundsEntity>().eq("sku_id", skuId));
        if (boundsEntity != null){
            SaleVO saleVO = new SaleVO();
            saleVO.setType("积分");
            StringBuffer sb = new StringBuffer();
            if (boundsEntity.getBuyBounds().intValue() > 0){
                sb.append("赠送的购物积分为" + boundsEntity.getBuyBounds());
            }
            if (boundsEntity.getGrowBounds().intValue() > 0){
                sb.append("赠送的成长积分为" + boundsEntity.getGrowBounds());
            }
            saleVO.setDesc(sb.toString());
            saleVOS.add(saleVO);
        }
        //打折
        SkuLadderEntity ladderEntity = this.skuLadderService.getOne(new QueryWrapper<SkuLadderEntity>().eq("sku_id", skuId));
        if (ladderEntity != null){
            SaleVO saleVO = new SaleVO();
            saleVO.setType("打折");
            saleVO.setDesc("满" + ladderEntity.getFullCount() + "件,打" + ladderEntity.getDiscount() + "折");
            saleVOS.add(saleVO);
        }
        //满减
        SkuFullReductionEntity fullReductionEntity = this.skuFullReductionService.getOne(new QueryWrapper<SkuFullReductionEntity>().eq("sku_id", skuId));
        if (fullReductionEntity != null){
            SaleVO saleVO = new SaleVO();
            saleVO.setType("满减");
            saleVO.setDesc("满" + fullReductionEntity.getFullPrice() + "元，减" + fullReductionEntity.getReducePrice() + "元");
            saleVOS.add(saleVO);
        }
        return saleVOS;
    }

}