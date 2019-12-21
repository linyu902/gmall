package com.atguigu.gmall.order.vo;

import com.atguigu.gmall.oms.vo.OrderItemVO;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-18 13:56
 * @version: 1.0
 * @modified By:十一。
 */
@Data
public class OrderConfirmVO {

    private List<MemberReceiveAddressEntity> addressEntities;

    private List<OrderItemVO> itemVOS;

    private Integer bounds;

    private String orderToken;
}
