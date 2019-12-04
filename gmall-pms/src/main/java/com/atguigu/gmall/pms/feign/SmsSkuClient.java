package com.atguigu.gmall.pms.feign;

import com.atguigu.gmall.api.SkuSaleApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-04 19:18
 * @version: 1.0
 * @modified By:十一。
 */
@FeignClient("sms-service")
public interface SmsSkuClient extends SkuSaleApi {

}
