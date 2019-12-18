package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.oms.api.GmallOmsApi;
import com.atguigu.gmall.ums.api.GmallUmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-14 13:23
 * @version: 1.0
 * @modified By:十一。
 */
@FeignClient("oms-service")
public interface GmallOmsClient extends GmallOmsApi {
}
