package com.atguigu.gmall.cart.interceptor;

import com.atguigu.core.utils.CookieUtils;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.gmall.cart.config.JwtProperties;
import com.atguigu.gmall.cart.pojo.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

/**
 * @description:这里我们使用了`ThreadLocal`来存储查询到的用户信息，线程内共享，因此请求到达`Controller`后可以共享User并且对外提供了静态的方法：`getUserInfo()`来获取User信息
 * @author: 十一。
 * @date: Created in 2019-12-17 12:29
 * @version: 1.0
 * @modified By:十一。
 */
@Component
@EnableConfigurationProperties(JwtProperties.class)
public class LoginInterceptor extends HandlerInterceptorAdapter {

    private static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<>();

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取cookie信息
        String tooken = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        String userKey = CookieUtils.getCookieValue(request, jwtProperties.getUserKey());
        // 如果都为空，设置userKey。
        if (StringUtils.isEmpty(userKey)){
            userKey = UUID.randomUUID().toString();
            CookieUtils.setCookie(request,response,jwtProperties.getUserKey(),userKey,6000000);
        }
        // 不管有没有登录都要设置userKey
        UserInfo userInfo = new UserInfo();
        userInfo.setUserKey(userKey);
        // token不为空,解析token
        if (StringUtils.isNotBlank(tooken)){
            Map<String, Object> info = JwtUtils.getInfoFromToken(tooken, jwtProperties.getPublicKey());
            if (!CollectionUtils.isEmpty(info)){
                String id = info.get("id").toString();
                userInfo.setId(new Long(id));
            }
        }
        // 保存到threadlocal
        THREAD_LOCAL.set(userInfo);
        // 如果token不为空，
        return true;
    }

    public static UserInfo getUserInfo() {
        return THREAD_LOCAL.get();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        THREAD_LOCAL.remove();
    }
}
