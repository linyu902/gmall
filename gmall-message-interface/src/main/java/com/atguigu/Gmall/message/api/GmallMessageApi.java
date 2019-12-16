package com.atguigu.Gmall.message.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-16 20:19
 * @version: 1.0
 * @modified By:十一。
 */
public interface GmallMessageApi {

    @GetMapping("message/sendmsg")
    public void sendMessage(@RequestParam("phoneNum")String phoneNum);
}
