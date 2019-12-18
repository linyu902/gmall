package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.api.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-14 13:23
 * @version: 1.0
 * @modified By:十一。
 */
@FeignClient("sms-service")
public interface GmallSmsClient extends GmallSmsApi {
}
