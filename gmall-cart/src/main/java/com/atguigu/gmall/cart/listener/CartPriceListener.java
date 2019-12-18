package com.atguigu.gmall.cart.listener;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.feign.GmallPmsClient;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-18 12:40
 * @version: 1.0
 * @modified By:十一。
 */
@Component
public class CartPriceListener {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GmallPmsClient pmsClient;

    private static final String PRICE_PREFIX = "gmall:price:";

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "gmall.item.update",durable = "true"),
                    exchange = @Exchange(
                            value = "GMALL-PMS-EXCHANGE",              //交换机名
                            ignoreDeclarationExceptions = "true",       //忽略
                            type = ExchangeTypes.TOPIC
                    ),
                    key = {"item.update"}
            )
    )
    public void listener(Long spuId){
        Resp<List<SkuInfoEntity>> listResp = this.pmsClient.querySkusBySpuId(spuId);
        List<SkuInfoEntity> skuInfoEntities = listResp.getData();
        skuInfoEntities.forEach(skuInfoEntity -> {
            redisTemplate.opsForValue().set(PRICE_PREFIX+skuInfoEntity.getSkuId(),skuInfoEntity.getPrice().toString());
        });
    }
}
