package com.atguigu.gmall.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 会员
 *
 * @author linyu902
 * @email linyu902@atguigu.com
 * @date 2019-12-02 10:48:09
 */
public interface MemberService extends IService<MemberEntity> {

    PageVo queryPage(QueryCondition params);

    Boolean check(String data, Integer type);

    void register(MemberEntity memberEntity, String code);

    MemberEntity queryByUsernameAndPassword(String username, String password);

    void getCode(String phone);
}

