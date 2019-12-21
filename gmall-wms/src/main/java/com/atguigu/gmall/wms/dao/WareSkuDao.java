package com.atguigu.gmall.wms.dao;

import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author linyu902
 * @email linyu902@atguigu.com
 * @date 2019-12-02 10:51:48
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    List<WareSkuEntity> checkStore(@Param("skuId") Long skuId,@Param("count") Integer count);

    int lockStore(@Param("id") Long wareSkuId, @Param("count") Integer count);

    int unLockStore(@Param("id") Long wareSkuId, @Param("count") Integer count);

    int minusStore(@Param("id") Long wareSkuId, @Param("count") Integer count);
}
