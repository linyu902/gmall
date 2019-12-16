package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-13 11:38
 * @version: 1.0
 * @modified By:十一。
 */
@Data
public class CategoryVO extends CategoryEntity {

    private List<CategoryEntity> subs;
}
