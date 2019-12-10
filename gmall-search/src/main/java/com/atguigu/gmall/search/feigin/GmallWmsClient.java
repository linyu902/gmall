package com.atguigu.gmall.search.feigin;

import com.atguigu.gmall.wms.api.GmallWmsApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-10 10:20
 * @version: 1.0
 * @modified By:十一。
 */
@FeignClient("wms-service")
public interface GmallWmsClient extends GmallWmsApi {

}
