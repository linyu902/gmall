package com.atguigu.gmall.wms.vo;

import lombok.Data;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-18 19:19
 * @version: 1.0
 * @modified By:十一。
 */
@Data
public class SkuLockVO {

    private Long skuId;

    private Integer count;

    private Boolean status; //锁定状态

    private Long wareSkuId;  //仓库id
}
