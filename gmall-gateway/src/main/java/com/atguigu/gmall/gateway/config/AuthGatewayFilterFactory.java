package com.atguigu.gmall.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-17 11:43
 * @version: 1.0
 * @modified By:十一。
 */
@Component
public class AuthGatewayFilterFactory extends AbstractGatewayFilterFactory {

    @Autowired
    private AuthGateWayFilter authGateWayFilter;

    @Override
    public GatewayFilter apply(Object config) {
        return authGateWayFilter;
    }
}
