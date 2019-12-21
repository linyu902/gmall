package com.atguigu.gmall.oms.listener;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.oms.dao.OrderDao;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.entity.OrderItemEntity;
import com.atguigu.gmall.oms.feign.GmallPmsClient;
import com.atguigu.gmall.oms.feign.GmallWmsClient;
import com.atguigu.gmall.oms.service.OrderItemService;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.gmall.ums.vo.BoundsVO;
import com.atguigu.gmall.wms.vo.SkuLockVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-20 20:03
 * @version: 1.0
 * @modified By:十一。
 */
@Component
public class OrderCloseListener {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private GmallPmsClient pmsClient;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private RedissonClient redissonClient;

    @RabbitListener(queues = "ORDER-DEAD-QUEUE")
    public void closeOrder(String orderToken){
        // 1.1 关单
        int result = this.orderDao.closeOrder(orderToken);
        if (result == 1){
            // 1.2 发送消息解锁库存
            this.amqpTemplate.convertAndSend("GMALL-STOCK-EXCHANGE","stock.unlock",orderToken);
        }
//        System.out.println("message = " + message);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "GMALL-ORDER-PAY",durable = "true"),
            exchange = @Exchange(value = "GMALL-ORDER-EXCHANGE",
                                    ignoreDeclarationExceptions = "true",
                                    type = ExchangeTypes.TOPIC),
            key = {"order.pay"}
    ))
    public void OrderPay(String orderToken){

        // 1. 修改订单状态
        Integer result = this.orderDao.setStatus(orderToken);
        if (result == 1){
            // 2. 发送消息减库存
            this.amqpTemplate.convertAndSend("GMALL-ORDER-EXCHANGE","stock.minus",orderToken);

            // 3. 发送消息给用户添加积分
            OrderEntity orderEntity = this.orderDao.selectOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderToken));
            BoundsVO boundsVO = new BoundsVO();
            boundsVO.setUserId(orderEntity.getMemberId());
            boundsVO.setGrowth(orderEntity.getGrowth());
            boundsVO.setIntegration(orderEntity.getIntegration());
            this.amqpTemplate.convertAndSend("GMALL-ORDER-EXCHANGE","user.bound", JSON.toJSONString(boundsVO));

        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "GMALL-ORDER-SECKILL",durable = "true"),
            exchange = @Exchange(value = "GMALL-ORDER-EXCHANGE",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"order.seckill"}
    ))
    public void seckillOrder(String voStr){

        SkuLockVO skuLockVO = JSON.parseObject(voStr, SkuLockVO.class);

        RCountDownLatch countDownLatch = redissonClient.getCountDownLatch("count:down:" + skuLockVO.getOrderToken());
        countDownLatch.trySetCount(1);

        //查询spuId
        OrderItemEntity itemEntity = new OrderItemEntity();
        itemEntity.setOrderSn(skuLockVO.getOrderToken());
        Resp<SkuInfoEntity> skuInfoEntityResp = this.pmsClient.querySkuById(skuLockVO.getSkuId());
        SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
        itemEntity.setSkuId(skuInfoEntity.getSkuId());
        itemEntity.setSkuName(skuInfoEntity.getSkuName());
        itemEntity.setSkuPrice(skuInfoEntity.getPrice());
        itemEntity.setSkuQuantity(1);
        Resp<List<SkuSaleAttrValueEntity>> saleAttrsResp = this.pmsClient.querySaleAttrsBySkuId(skuInfoEntity.getSkuId());
        List<SkuSaleAttrValueEntity> saleAttr = saleAttrsResp.getData();
        itemEntity.setSkuAttrsVals(JSON.toJSONString(saleAttr));
        Long spuId = skuInfoEntity.getSpuId();
        Resp<SpuInfoEntity> spuInfoEntityResp = this.pmsClient.querySpuById(spuId);
        SpuInfoEntity spuInfoEntity = spuInfoEntityResp.getData();
        itemEntity.setSpuId(spuId);
        itemEntity.setSpuName(spuInfoEntity.getSpuName());
        Resp<BrandEntity> brandEntityResp = this.pmsClient.queryBrandName(spuInfoEntity.getBrandId());
        BrandEntity brandEntity = brandEntityResp.getData();
        itemEntity.setSpuBrand(brandEntity.getName());
        itemEntity.setCategoryId(spuInfoEntity.getCatalogId());
        this.orderItemService.save(itemEntity);
        countDownLatch.countDown();
    }
}
