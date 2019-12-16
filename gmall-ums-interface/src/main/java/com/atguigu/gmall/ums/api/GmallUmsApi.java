package com.atguigu.gmall.ums.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.ums.entity.MemberEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-16 16:48
 * @version: 1.0
 * @modified By:十一。
 */
public interface GmallUmsApi {
    @GetMapping("ums/member/query")
    public Resp<MemberEntity> queryByUsernameAndPassword(@RequestParam("username")String username, @RequestParam("password")String password);
}
