package com.atguigu.gmall.order.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.atguigu.core.bean.Resp;
import com.atguigu.core.exception.OrderException;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.entity.OrderItemEntity;
import com.atguigu.gmall.order.feign.GmallOmsClient;
import com.atguigu.gmall.order.pay.AlipayTemplate;
import com.atguigu.gmall.order.pay.PayAsyncVo;
import com.atguigu.gmall.order.pay.PayVo;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.vo.OrderConfirmVO;
import com.atguigu.gmall.oms.vo.OrderSubmitVO;
import com.atguigu.gmall.wms.vo.SkuLockVO;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-18 15:05
 * @version: 1.0
 * @modified By:十一。
 */
@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AlipayTemplate alipayTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private GmallOmsClient omsClient;

    @GetMapping("confirm")
    public Resp<OrderConfirmVO> getConfirm(){
        OrderConfirmVO orderConfirmVO = orderService.getConfirm();
        return Resp.ok(orderConfirmVO);
    }

    @PostMapping("submit")
    public Resp<Object> submit(@RequestBody OrderSubmitVO submitVO){

        OrderEntity orderEntity = this.orderService.submit(submitVO);

        try {
            PayVo payVo = new PayVo();
            payVo.setOut_trade_no(orderEntity.getOrderSn());
            payVo.setSubject("谷粒商城");
            payVo.setTotal_amount(orderEntity.getTotalAmount().intValue() == 0? "100" : orderEntity.getTotalAmount().toString());
            payVo.setBody("咱俩完了，真的。");
            String form = alipayTemplate.pay(payVo);
            System.out.println("form = " + form);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return Resp.ok(null);
    }

    @PostMapping("pay/success")
    public Resp<Object> paySuccess(PayAsyncVo payAsyncVo){

        String orderToken = payAsyncVo.getOut_trade_no();

        this.amqpTemplate.convertAndSend("GMALL-ORDER-EXCHANGE","order.pay",orderToken);

        return Resp.ok(null);
    }

    @PostMapping("seckill/{skuId}")
    public Resp<Object> secKill(@PathVariable("skuId")Long skuId){

        // tips: 防止分布式事务问题，加锁
        RSemaphore semaphore = redissonClient.getSemaphore("semapnore:lock:" + skuId);
        semaphore.trySetPermits(500);
        if (semaphore.tryAcquire(1)){
            // 1. 查询库存
            String stockStr = this.redisTemplate.opsForValue().get("stock:seckill:" + skuId);
            int stock = Integer.parseInt(stockStr);
            if (stock == 0){
                throw new OrderException("秒杀已经结束.");
            }

            // 2. 有库存，进行秒杀,减Redis中的库存
            --stock;
            this.redisTemplate.opsForValue().set("stock:seckill" + skuId,String.valueOf(stock));

            // 3. 发送消息，让数据库真正减库存
            SkuLockVO vo = new SkuLockVO();
            vo.setSkuId(skuId);
            vo.setCount(1);
            String orderToken = IdWorker.getIdStr();
            vo.setOrderToken(orderToken);
            this.amqpTemplate.convertAndSend("GMALL-ORDER-EXCHANGE","order.seckill", JSON.toJSONString(vo));
            semaphore.release();
            return Resp.ok("秒杀成功！");
        }
        return Resp.ok("再接再厉！！！");
    }

    @GetMapping("seckill/query/{orderToken}")
    public Resp<OrderItemEntity> querySeckillOrder(@PathVariable("orderToken")String orderToken) throws InterruptedException {

//        RCountDownLatch countDownLatch = redissonClient.getCountDownLatch("count:down:" + orderToken);
//        countDownLatch.wait();

        Resp<OrderItemEntity> itemEntityResp = this.omsClient.querySeckillOrder(orderToken);

        return itemEntityResp;
    }
}
