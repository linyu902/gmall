package com.atguigu.gmall.oms.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.oms.entity.OrderItemEntity;
import com.atguigu.gmall.oms.feign.GmallPmsClient;
import com.atguigu.gmall.oms.feign.GmallSmsClient;
import com.atguigu.gmall.oms.feign.GmallUmsClient;
import com.atguigu.gmall.oms.feign.GmallWmsClient;
import com.atguigu.gmall.oms.service.OrderItemService;
import com.atguigu.gmall.oms.vo.OrderItemVO;
import com.atguigu.gmall.oms.vo.OrderSubmitVO;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.oms.dao.OrderDao;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.service.OrderService;
import org.springframework.transaction.annotation.Transactional;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private GmallPmsClient pmsClient;

    @Autowired
    private GmallUmsClient umsClient;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private OrderItemService orderItemService;
    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageVo(page);
    }

    @Transactional
    @Override
    public OrderEntity bigSave(OrderSubmitVO orderSubmitVO) {

        // 1. 保存订单信息
        OrderEntity orderEntity = new OrderEntity();


        Long userId = orderSubmitVO.getUserId();
        Resp<MemberEntity> memberEntityResp = this.umsClient.queryMemberById(userId);
        MemberEntity memberEntity = memberEntityResp.getData();
        orderEntity.setMemberId(userId);
        orderEntity.setOrderSn(orderSubmitVO.getOrderToken());
        orderEntity.setMemberUsername(memberEntity.getUsername());
        orderEntity.setAutoConfirmDay(7);
        orderEntity.setCreateTime(new Date());
        orderEntity.setModifyTime(orderEntity.getCreateTime());
        orderEntity.setConfirmStatus(0);
        orderEntity.setTotalAmount(orderSubmitVO.getTotalPrice());
        orderEntity.setPayAmount(orderSubmitVO.getTotalPrice());
        orderEntity.setPayType(1);
        orderEntity.setSourceType(0);
        orderEntity.setStatus(0);
        orderEntity.setDeliveryCompany(orderSubmitVO.getDeliveryCompany());
        MemberReceiveAddressEntity addressEntity = orderSubmitVO.getAddressEntity();
        orderEntity.setReceiverCity(addressEntity.getCity());
        orderEntity.setReceiverDetailAddress(addressEntity.getDetailAddress());
        orderEntity.setReceiverName(addressEntity.getName());
        orderEntity.setReceiverPhone(addressEntity.getPhone());
        orderEntity.setReceiverPostCode(addressEntity.getPostCode());
        orderEntity.setReceiverProvince(addressEntity.getProvince());
        orderEntity.setReceiverRegion(addressEntity.getRegion());
        orderEntity.setDeleteStatus(0);

        this.save(orderEntity);
        // 2. 保存订单详情信息
        List<OrderItemVO> itemVOS = orderSubmitVO.getItemVOS();
        itemVOS.forEach(orderItemVO -> {
            OrderItemEntity itemEntity = new OrderItemEntity();
            itemEntity.setOrderId(orderEntity.getId());
            itemEntity.setOrderSn(orderSubmitVO.getOrderToken());
            //查询spuId
            Resp<SkuInfoEntity> skuInfoEntityResp = this.pmsClient.querySkuById(orderItemVO.getSkuId());
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            itemEntity.setSkuId(skuInfoEntity.getSkuId());
            itemEntity.setSkuName(skuInfoEntity.getSkuName());
            itemEntity.setSkuPrice(skuInfoEntity.getPrice());
            itemEntity.setSkuQuantity(orderItemVO.getCount());
            Resp<List<SkuSaleAttrValueEntity>> saleAttrsResp = this.pmsClient.querySaleAttrsBySkuId(orderItemVO.getSkuId());
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
        });

//        int i = 1 / 0;
        //发送消息到延迟队列，定时关dan
        String orderToken = orderSubmitVO.getOrderToken();
        amqpTemplate.convertAndSend("GMALL-ORDER-EXCHANGE","order.ttl",orderToken);

        return orderEntity;
    }

}