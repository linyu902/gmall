package com.atguigu.gmall.search.feigin;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-10 10:19
 * @version: 1.0
 * @modified By:十一。
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {
}
