package com.atguigu.gmall.cart.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-14 13:23
 * @version: 1.0
 * @modified By:十一。
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {
}
