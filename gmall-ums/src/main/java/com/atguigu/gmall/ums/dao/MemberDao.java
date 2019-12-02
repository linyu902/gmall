package com.atguigu.gmall.ums.dao;

import com.atguigu.gmall.ums.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author linyu902
 * @email linyu902@atguigu.com
 * @date 2019-12-02 10:48:09
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
