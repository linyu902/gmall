package com.atguigu.gmall.auth.service;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.gmall.auth.config.JwtProperties;
import com.atguigu.gmall.auth.feign.GmallUmsClient;
import com.atguigu.gmall.ums.entity.MemberEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-16 18:06
 * @version: 1.0
 * @modified By:十一。
 */
@Service
public class AuthService {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private GmallUmsClient gmallUmsClient;


    public String accredit(String username, String password) {

        //远程调用验证用户是否存在
        Resp<MemberEntity> memberEntityResp = gmallUmsClient.queryByUsernameAndPassword(username, password);
        MemberEntity memberEntity = memberEntityResp.getData();
        //为空返回NUll
        if (memberEntity == null) {
            return null;
        }
        //制作token
        String token = null;
        try {
            Map<String,Object> map = new HashMap<>();
            map.put("id",memberEntity.getId());
            map.put("username",memberEntity.getUsername());
            token = JwtUtils.generateToken(map, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }
}
