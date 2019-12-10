package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-04 12:53
 * @version: 1.0
 * @modified By:十一。
 */
@Data
@ToString
public class SpuInfoVO extends SpuInfoEntity {

    private List<String> spuImages;

    private List<BaseAttrVO> baseAttrs;

    private List<SkuInfoVO> skus;

}
