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
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private static final String KEY_PREFIX = "gmall:cart:";

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
        // 1. 当收到消息时，修改redis中的价格                消息在SpuInfoController中发送
        Resp<List<SkuInfoEntity>> listResp = this.pmsClient.querySkusBySpuId(spuId);
        List<SkuInfoEntity> skuInfoEntities = listResp.getData();
        skuInfoEntities.forEach(skuInfoEntity -> {
            redisTemplate.opsForValue().set(PRICE_PREFIX+skuInfoEntity.getSkuId(),skuInfoEntity.getPrice().toString());
        });
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "GMALL.CART.DELETE",durable = "true"),
            exchange = @Exchange(value = "GMALL-PMS-EXCHANGE"
                                ,ignoreDeclarationExceptions = "true"
                                ,type = ExchangeTypes.TOPIC),
            key = {"cart.delete"}
    ))
    public void deleteCart(Map<String,Object> map){
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(KEY_PREFIX + map.get("userId"));

        List<Object> skuIdObj = (List<Object>) map.get("skuIds");

        List<String> skuIds = skuIdObj.stream().map(sku -> {
            return sku.toString();
        }).collect(Collectors.toList());
        hashOps.delete(skuIds.toArray());
    }
}
