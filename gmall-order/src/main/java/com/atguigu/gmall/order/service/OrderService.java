package com.atguigu.gmall.order.service;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.exception.OrderException;
import com.atguigu.gmall.cart.pojo.Cart;
import com.atguigu.gmall.cart.pojo.UserInfo;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.order.feign.*;
import com.atguigu.gmall.order.interceptor.LoginInterceptor;
import com.atguigu.gmall.order.vo.OrderConfirmVO;
import com.atguigu.gmall.oms.vo.OrderItemVO;
import com.atguigu.gmall.oms.vo.OrderSubmitVO;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import com.atguigu.gmall.vo.SaleVO;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.vo.SkuLockVO;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-18 15:05
 * @version: 1.0
 * @modified By:十一。
 */
@Service
public class OrderService {

    @Autowired
    private GmallPmsClient pmsClient;

    @Autowired
    private GmallUmsClient umsClient;

    @Autowired
    private GmallSmsClient smsClient;

    @Autowired
    private GmallOmsClient omsClient;

    @Autowired
    private GmallWmsClient wmsClient;

    @Autowired
    private GmallCartClient cartClient;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;

    public static final String KEY_PREFIX = "gmall:order:";

    public OrderConfirmVO getConfirm() {

        OrderConfirmVO confirmVO = new OrderConfirmVO();
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        Long userId = userInfo.getId();
        if (userId == null){
            return null;
        }
        CompletableFuture<Void> addressCompletableFuture = CompletableFuture.runAsync(() -> {
            //地址信息
            Resp<List<MemberReceiveAddressEntity>> listResp = umsClient.queryReceiveAddressEntities(userId);
            List<MemberReceiveAddressEntity> addressEntities = listResp.getData();
            confirmVO.setAddressEntities(addressEntities);
        }, threadPoolExecutor);

        CompletableFuture<Void> itemVOSCompletableFuture = CompletableFuture.runAsync(() -> {
            //购物车中商品列表
            Resp<List<Cart>> cartsResp = this.cartClient.getAllCarts(userId);
            List<Cart> carts = cartsResp.getData();
            List<OrderItemVO> itemVOS = carts.stream().map(cart -> {
                OrderItemVO itemVO = new OrderItemVO();
                Long skuId = cart.getSkuId();
                itemVO.setSkuId(skuId);
                itemVO.setCount(cart.getCount());
                CompletableFuture<Void> skuCompletableFuture = CompletableFuture.runAsync(() -> {
                    //查询sku
                    Resp<SkuInfoEntity> skuInfoEntityResp = pmsClient.querySkuById(skuId);
                    SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
                    itemVO.setDefaultImage(skuInfoEntity.getSkuDefaultImg());
                    itemVO.setPrice(skuInfoEntity.getPrice());
                    itemVO.setTitle(skuInfoEntity.getSkuTitle());
                }, threadPoolExecutor);

                CompletableFuture<Void> saleAttrCompletableFuture = CompletableFuture.runAsync(() -> {
                    //查询sku的销售属性
                    Resp<List<SkuSaleAttrValueEntity>> saleAttrsResp = pmsClient.querySaleAttrsBySkuId(skuId);
                    List<SkuSaleAttrValueEntity> saleAttrs = saleAttrsResp.getData();
                    itemVO.setSaleAttrs(saleAttrs);
                }, threadPoolExecutor);

                CompletableFuture<Void> salesCompletableFuture = CompletableFuture.runAsync(() -> {
                    //查询优惠
                    Resp<List<SaleVO>> salesResp = smsClient.queryBoundsAndFullAndLadder(skuId);
                    List<SaleVO> saleVOS = salesResp.getData();
                    itemVO.setSaleVOS(saleVOS);
                }, threadPoolExecutor);

                CompletableFuture<Void> storeCompletableFuture = CompletableFuture.runAsync(() -> {
                    //查询库存
                    Resp<List<WareSkuEntity>> resp = wmsClient.queryWareSkuBySkuId(skuId);
                    List<WareSkuEntity> wareSkuEntities = resp.getData();
                    itemVO.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0));
                }, threadPoolExecutor);

                CompletableFuture.allOf(skuCompletableFuture, saleAttrCompletableFuture, salesCompletableFuture, storeCompletableFuture).join();

                return itemVO;
            }).collect(Collectors.toList());
            confirmVO.setItemVOS(itemVOS);
        }, threadPoolExecutor);

        CompletableFuture<Void> boundsCompletableFuture = CompletableFuture.runAsync(() -> {
            //积分
            Resp<MemberEntity> memberEntityResp = umsClient.queryMemberById(userId);
            MemberEntity memberEntity = memberEntityResp.getData();
            confirmVO.setBounds(memberEntity.getIntegration());
        }, threadPoolExecutor);

        CompletableFuture<Void> tokenCompletableFuture = CompletableFuture.runAsync(() -> {
            //token
            String token = IdWorker.getIdStr();
            confirmVO.setOrderToken(token);
            redisTemplate.opsForValue().set(KEY_PREFIX + token,token);
        }, threadPoolExecutor);

        CompletableFuture.allOf(addressCompletableFuture,itemVOSCompletableFuture,boundsCompletableFuture,tokenCompletableFuture).join();

        return confirmVO;
    }

    @Transactional
    public Object submit(OrderSubmitVO submitVO) {

        //  1.  验证token，防止重复提交
        String orderToken = submitVO.getOrderToken();
        String token = redisTemplate.opsForValue().get(KEY_PREFIX + orderToken);
        redisTemplate.delete(KEY_PREFIX + orderToken);
        if (token == null || !StringUtils.equals(token,orderToken)){
            throw new OrderException("请勿重复提交订单。。。。");
        }
        //  2.  校验总价格
        List<OrderItemVO> itemVOS = submitVO.getItemVOS();
        BigDecimal totalPrice = submitVO.getTotalPrice();
        BigDecimal realTotalPrice = new BigDecimal(0);
        for (OrderItemVO orderItemVO : itemVOS) {
            Long skuId = orderItemVO.getSkuId();
            Resp<SkuInfoEntity> skuInfoEntityResp = pmsClient.querySkuById(skuId);
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            BigDecimal price = skuInfoEntity.getPrice().multiply(new BigDecimal(orderItemVO.getCount())) ;
            realTotalPrice = realTotalPrice.add(price);
        }
        if (totalPrice.intValue() != realTotalPrice.intValue()){
            throw  new OrderException("网络开小差了，请刷新重试.....");
        }
        //  3.  校验库存，查询加锁定  Redisson
        List<SkuLockVO> skuLockVOS = itemVOS.stream().map(orderItemVO -> {
            SkuLockVO skuLockVO = new SkuLockVO();
            skuLockVO.setSkuId(orderItemVO.getSkuId());
            skuLockVO.setCount(orderItemVO.getCount());
            skuLockVO.setOrderToken(orderToken);
            return skuLockVO;
        }).collect(Collectors.toList());
        Resp<Object> resp = wmsClient.checkStore(skuLockVOS);
        if (resp.getData() != null){
            throw new OrderException((String) resp.getData());
        }
        //  4.  创建订单即订单详情(编写接口)
        try {
            Resp<OrderEntity> orderEntityResp = this.omsClient.bigSave(submitVO);
            OrderEntity orderEntity = orderEntityResp.getData();
        } catch (Exception e) {
            e.printStackTrace();
            //  出现异常，发送消息将锁定的库存解锁..
            this.amqpTemplate.convertAndSend("GMALL-STOCK-EXCHANGE","stock.unlock",orderToken);
            throw new OrderException("网络连接错误，请稍后再试....");
        }

        //  5.  删除购物车
        Map<String,Object> map = new HashMap<>();
        map.put("userId",submitVO.getUserId());
        List<String> skuIds = itemVOS.stream().map(orderItemVO -> {
            Long skuId = orderItemVO.getSkuId();
            return skuId.toString();
        }).collect(Collectors.toList());
        map.put("skuIds",skuIds);
        // 5.2. 发送消息给购物车
        amqpTemplate.convertAndSend("GMALL-PMS-EXCHANGE","cart.delete",map);

        return null;
    }
}
