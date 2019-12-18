package com.atguigu.gmall.wms.service.impl;

import com.atguigu.gmall.wms.vo.SkuLockVO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.wms.dao.WareSkuDao;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.service.WareSkuService;
import org.springframework.util.CollectionUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private WareSkuDao wareSkuDao;

    @Autowired
    private RedissonClient redissonClient;

    private static final String LOCK_PREFIX = "store:lock:";

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public String checkStore(List<SkuLockVO> skuLockVOS) {

        if (CollectionUtils.isEmpty(skuLockVOS)){
            return "你还未勾选商品！";
        }
        skuLockVOS.forEach(skuLockVO -> {
            //查询库存信息并锁定
            getStore(skuLockVO);
        });
        List<SkuLockVO> unLock = skuLockVOS.stream().filter(skuLockVO -> skuLockVO.getStatus() == false).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(unLock)){
            //解锁
            List<SkuLockVO> lockVOS = skuLockVOS.stream().filter(skuLockVO -> skuLockVO.getStatus()).collect(Collectors.toList());
            lockVOS.forEach(skuLockVO -> {
                this.wareSkuDao.unLockStore(skuLockVO.getWareSkuId(),skuLockVO.getCount());
            });
            //提示数量不足的商品
            List<Long> skuIds = unLock.stream().map(skuLockVO -> skuLockVO.getSkuId()).collect(Collectors.toList());
            return "该商品数量不足" + skuIds.toString();
        }
        return null;
    }

    private void getStore(SkuLockVO skuLockVO){
        RLock lock = redissonClient.getLock(LOCK_PREFIX + skuLockVO.getSkuId().toString());
        lock.lock();
        //查询库存
        List<WareSkuEntity> wareSkuEntities = this.wareSkuDao.checkStore(skuLockVO.getSkuId(),skuLockVO.getCount());
        if (!CollectionUtils.isEmpty(wareSkuEntities)){
            WareSkuEntity wareSkuEntity = wareSkuEntities.get(0);
            skuLockVO.setWareSkuId(wareSkuEntity.getId());
            //锁定库存
            this.wareSkuDao.lockStore(skuLockVO.getWareSkuId(),skuLockVO.getCount());
            skuLockVO.setStatus(true);
        }else {
            skuLockVO.setStatus(false);
        }
        lock.unlock();
    }
}