package com.atguigu.gmall.pms.dao;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author linyu902
 * @email linyu902@atguigu.com
 * @date 2019-12-03 13:44:41
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
