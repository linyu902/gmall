package com.atguigu.gmall.oms.vo;

import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-18 18:37
 * @version: 1.0
 * @modified By:十一。
 */
@Data
public class OrderSubmitVO {

    private String orderToken;      //防止重复提交

    private MemberReceiveAddressEntity addressEntity;

    private Integer payType;        //支付方式

    private String deliveryCompany; //配送方式

    private List<OrderItemVO> itemVOS;

    private Integer bounds;

    private BigDecimal totalPrice;  //价格校验

    private Long userId;
}
