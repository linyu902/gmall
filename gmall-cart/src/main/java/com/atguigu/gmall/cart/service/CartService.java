package com.atguigu.gmall.cart.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.feign.GmallPmsClient;
import com.atguigu.gmall.cart.feign.GmallSmsClient;
import com.atguigu.gmall.cart.feign.GmallWmsClient;
import com.atguigu.gmall.cart.interceptor.LoginInterceptor;
import com.atguigu.gmall.cart.pojo.Cart;
import com.atguigu.gmall.cart.pojo.UserInfo;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.vo.SaleVO;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-17 15:21
 * @version: 1.0
 * @modified By:十一。
 */
@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GmallPmsClient pmsClient;

    @Autowired
    private GmallSmsClient smsClient;

    @Autowired
    private GmallWmsClient wmsClient;

    private static final String KEY_PREFIX = "gmall:cart:";
    private static final String PRICE_PREFIX = "gmall:price:";
    public void add(Cart cart) {

        //判断是否登陆
        String key = KEY_PREFIX;
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        if (userInfo.getId() == null) {
            key += userInfo.getUserKey();
        }else {
            key += userInfo.getId();
        }

        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);

        Long skuId = cart.getSkuId();
        Integer count = cart.getCount();
        //已存在该商品，更新数量
        if (hashOps.hasKey(skuId.toString())){
            String cartJson = hashOps.get(skuId.toString()).toString();
            cart = JSON.parseObject(cartJson, Cart.class);
            cart.setCount(count + cart.getCount());
            hashOps.put(skuId.toString(),JSON.toJSONString(cart));
        }else {
            //不存在该商品，新增
            Resp<SkuInfoEntity> skuInfoEntityResp = pmsClient.querySkuById(skuId);
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            cart.setPrice(skuInfoEntity.getPrice());
            cart.setTitle(skuInfoEntity.getSkuTitle());
            cart.setDefaultImage(skuInfoEntity.getSkuDefaultImg());
            cart.setCurrentPrice(skuInfoEntity.getPrice());
            //价格存入redis一份
            redisTemplate.opsForValue().set(PRICE_PREFIX + skuId,skuInfoEntity.getPrice().toString());

            Resp<List<SkuSaleAttrValueEntity>> saleAttrsResp = pmsClient.querySaleAttrsBySkuId(skuId);
            List<SkuSaleAttrValueEntity> saleAttrs = saleAttrsResp.getData();
            cart.setSaleAttrs(saleAttrs);

            Resp<List<SaleVO>> listResp = smsClient.queryBoundsAndFullAndLadder(skuId);
            List<SaleVO> saleVOS = listResp.getData();
            cart.setSaleVOS(saleVOS);

            Resp<List<WareSkuEntity>> resp = wmsClient.queryWareSkuBySkuId(skuId);
            List<WareSkuEntity> data = resp.getData();
            boolean bool = data.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0);
            cart.setStore(bool);
            cart.setCheck(true);
            hashOps.put(cart.getSkuId().toString(),JSON.toJSONString(cart));
        }
    }

    public List<Cart> list() {
        //查询未登录的购物车
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        List<Cart> carts = null;
        String key = KEY_PREFIX + userInfo.getUserKey();
        BoundHashOperations<String, Object, Object> unLoginHashOps = redisTemplate.boundHashOps(key);
        List<Object> cartJsons = unLoginHashOps.values();
        if (!CollectionUtils.isEmpty(cartJsons)){
            carts = cartJsons.stream().map(cartJson -> {
                Cart cart = JSON.parseObject(cartJson.toString(), Cart.class);
                String priceString = redisTemplate.opsForValue().get(PRICE_PREFIX + cart.getSkuId());
                cart.setCurrentPrice(new BigDecimal(priceString));
                return cart;
            }).collect(Collectors.toList());
        }
        //判断是否登录
        if (userInfo.getId() == null) {
            return carts;
        }
        //登陆后的购物车
        String loginkey = KEY_PREFIX + userInfo.getId();
        BoundHashOperations<String, Object, Object> loginHashOps = redisTemplate.boundHashOps(loginkey);
        // 1.1.如果游客购物车不为空，合并购物车
        if (!CollectionUtils.isEmpty(carts)){
            carts.forEach(cart -> {
                // 1.2 存在则增加数量
                Integer count = cart.getCount();
                if (loginHashOps.hasKey(cart.getSkuId().toString())){
                    String cartJson = loginHashOps.get(cart.getSkuId().toString()).toString();
                    cart = JSON.parseObject(cartJson, Cart.class);
                    cart.setCount(cart.getCount() + count);
                }
                //不存在放入
                loginHashOps.put(cart.getSkuId().toString(),JSON.toJSONString(cart));
            });
            //合并后删除临时购物车
            redisTemplate.delete(key);
        } else {
            //  为空则获取登陆购物车
            List<Object> loginCartsJson = loginHashOps.values();
            carts =  loginCartsJson.stream().map(cartJson -> {
                Cart cart = JSON.parseObject(cartJson.toString(), Cart.class);
                String priceString = redisTemplate.opsForValue().get(PRICE_PREFIX + cart.getSkuId());
                cart.setCurrentPrice(new BigDecimal(priceString));
                return cart;
            }).collect(Collectors.toList());
        }

        return carts;
    }

    public void updateCount(Cart cart) {
        //判断是否登陆
        String key = KEY_PREFIX;
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        if (userInfo.getId() == null) {
            key += userInfo.getUserKey();
        }else {
            key += userInfo.getId();
        }
        // 1.1. 获取购物车
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);
        // 1.2. 修改商品数量
        Integer count = cart.getCount();
        if (hashOps.hasKey(cart.getSkuId().toString())){
            String cartJson = hashOps.get(cart.getSkuId().toString()).toString();
            cart = JSON.parseObject(cartJson, Cart.class);
            cart.setCount(count);
            hashOps.put(cart.getSkuId().toString(),JSON.toJSONString(cart));
        }

    }

    public void deleteBySkuId(Long skuId) {
        //判断是否登陆
        String key = KEY_PREFIX;
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        if (userInfo.getId() == null) {
            key += userInfo.getUserKey();
        }else {
            key += userInfo.getId();
        }
        //  获取购物车
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);
        //  判断是否存在
        if (hashOps.hasKey(skuId.toString())){
            //  存在则删除
            hashOps.delete(skuId.toString());
        }
    }

    public List<Cart> getAllcarts(Long userId) {
        String key = KEY_PREFIX + userId;
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);
        List<Object> values = hashOps.values();
        List<Cart> carts = values.stream().map(cartJson -> JSON.parseObject(cartJson.toString(), Cart.class))
                .filter(Cart::getCheck).collect(Collectors.toList());
        return carts;
    }
}
