package com.atguigu.gmall.oms.controller;

import java.util.Arrays;
import java.util.Map;


import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gmall.oms.entity.OrderItemEntity;
import com.atguigu.gmall.oms.service.OrderItemService;




/**
 * 订单项信息
 *
 * @author linyu902
 * @email linyu902@atguigu.com
 * @date 2019-12-02 10:21:12
 */
@Api(tags = "订单项信息 管理")
@RestController
@RequestMapping("oms/orderitem")
public class OrderItemController {
    @Autowired
    private OrderItemService orderItemService;

    @GetMapping("{orderToken}")
    public Resp<OrderItemEntity> querySeckillOrder(@PathVariable("orderToken")String orderToken){

        OrderItemEntity itemEntity = this.orderItemService.getOne(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderToken));

        return Resp.ok(itemEntity);
    }

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('oms:orderitem:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = orderItemService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('oms:orderitem:info')")
    public Resp<OrderItemEntity> info(@PathVariable("id") Long id){
		OrderItemEntity orderItem = orderItemService.getById(id);

        return Resp.ok(orderItem);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('oms:orderitem:save')")
    public Resp<Object> save(@RequestBody OrderItemEntity orderItem){
		orderItemService.save(orderItem);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('oms:orderitem:update')")
    public Resp<Object> update(@RequestBody OrderItemEntity orderItem){
		orderItemService.updateById(orderItem);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('oms:orderitem:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids){
		orderItemService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
