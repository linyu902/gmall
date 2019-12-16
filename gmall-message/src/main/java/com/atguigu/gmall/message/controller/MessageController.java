package com.atguigu.gmall.message.controller;

import com.atguigu.gmall.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-16 20:01
 * @version: 1.0
 * @modified By:十一。
 */
@RestController
@RequestMapping("message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("sendmsg")
    public void sendMessage(@RequestParam("phoneNum")String phoneNum){
        messageService.sendMessage(phoneNum);
    }
}
