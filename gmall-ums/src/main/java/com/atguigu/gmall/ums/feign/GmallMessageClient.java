package com.atguigu.gmall.ums.feign;

import com.atguigu.Gmall.message.api.GmallMessageApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-16 20:29
 * @version: 1.0
 * @modified By:十一。
 */
@FeignClient("message-service")
public interface GmallMessageClient extends GmallMessageApi {

}
