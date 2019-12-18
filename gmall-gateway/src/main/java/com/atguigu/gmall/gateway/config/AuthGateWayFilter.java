package com.atguigu.gmall.gateway.config;

import com.atguigu.core.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-17 11:34
 * @version: 1.0
 * @modified By:十一。
 */
@Component
@EnableConfigurationProperties(JwtProperties.class)
public class AuthGateWayFilter implements GatewayFilter {

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 获取request和response，注意：不是HttpServletRequest及HttpServletResponse
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 获取所有cookie
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        // 如果cookies为空或者不包含指定的token，则相应认证未通过
        if (CollectionUtils.isEmpty(cookies) || !cookies.containsKey(jwtProperties.getCookieName())){
            // 响应未认证！
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            // 结束请求
            return response.setComplete();
        }
        // 获取cookie
        HttpCookie cookie = cookies.getFirst(jwtProperties.getCookieName());
        // 校验cookie
        try {
            Map<String, Object> info = JwtUtils.getInfoFromToken(cookie.getValue(), jwtProperties.getPublicKey());
        } catch (Exception e) {
            // 校验失败，响应未认证
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            // 结束请求
            return response.setComplete();
        }
        // 认证通过放行
        return chain.filter(exchange);
    }
}
