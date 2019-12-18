package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.cart.api.GmallCartApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-18 16:35
 * @version: 1.0
 * @modified By:十一。
 */
@FeignClient("cart-service")
public interface GmallCartClient extends GmallCartApi {
}
