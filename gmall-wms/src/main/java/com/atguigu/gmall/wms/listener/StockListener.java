package com.atguigu.gmall.wms.listener;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.wms.dao.WareSkuDao;
import com.atguigu.gmall.wms.vo.SkuLockVO;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-20 18:28
 * @version: 1.0
 * @modified By:十一。
 */
@Component
public class StockListener {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private WareSkuDao wareSkuDao;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "GMALL.STOCK.UNLOCK",durable = "true"),
            exchange = @Exchange(value = "GMALL-STOCK-EXCHANGE"
                                ,ignoreDeclarationExceptions = "true"
                                ,type = ExchangeTypes.TOPIC),
            key = {"stock.unlock"}
    ))
    public void unlockStock(String orderToken){
        String skuLockStr = redisTemplate.opsForValue().get(orderToken);

        List<SkuLockVO> skuLockVOS = JSON.parseArray(skuLockStr, SkuLockVO.class);
        if (!CollectionUtils.isEmpty(skuLockVOS)){
            skuLockVOS.forEach(skuLockVO -> {
                this.wareSkuDao.unLockStore(skuLockVO.getWareSkuId(),skuLockVO.getCount());
            });
        }
        redisTemplate.delete(orderToken);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "GMALL.STOCK.MINUS",durable = "true"),
            exchange = @Exchange(value = "GMALL-ORDER-EXCHANGE"
                    ,ignoreDeclarationExceptions = "true"
                    ,type = ExchangeTypes.TOPIC),
            key = {"stock.minus"}
    ))
    public void minusStock(String orderToken){
        String skuLockStr = redisTemplate.opsForValue().get(orderToken);

        List<SkuLockVO> skuLockVOS = JSON.parseArray(skuLockStr, SkuLockVO.class);

        skuLockVOS.forEach(skuLockVO -> {
            this.wareSkuDao.minusStore(skuLockVO.getWareSkuId(),skuLockVO.getCount());
        });

        redisTemplate.delete(orderToken);
    }
}
