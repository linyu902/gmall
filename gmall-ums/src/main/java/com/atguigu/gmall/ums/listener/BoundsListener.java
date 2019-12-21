package com.atguigu.gmall.ums.listener;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.ums.dao.MemberDao;
import com.atguigu.gmall.ums.vo.BoundsVO;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-21 17:40
 * @version: 1.0
 * @modified By:十一。
 */
@Component
public class BoundsListener {

    @Autowired
    private MemberDao memberDao;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ORDER-UMS-BOUNDS",durable = "true"),
            exchange = @Exchange(value = "GMALL-ORDER-EXCHANGE",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = {"user.bound"}
    ))
    public void addBounds(String boundsVOJson){
        BoundsVO boundsVO = JSON.parseObject(boundsVOJson, BoundsVO.class);
        this.memberDao.updateBoundsById(boundsVO.getUserId(),boundsVO.getGrowth(),boundsVO.getIntegration());
    }
}
