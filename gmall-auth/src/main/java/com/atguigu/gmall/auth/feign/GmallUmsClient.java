package com.atguigu.gmall.auth.feign;

import com.atguigu.gmall.ums.api.GmallUmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-16 17:51
 * @version: 1.0
 * @modified By:十一。
 */
@FeignClient("ums-service")
public interface GmallUmsClient extends GmallUmsApi {

}
