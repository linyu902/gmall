package com.atguigu.gmall.wms;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.ws.Action;

@SpringBootTest
class GmallWmsApplicationTests {

//    @Autowired
//    AmqpTemplate amqpTemplate;

    @Test
    void contextLoads() {

//        amqpTemplate.convertAndSend("GMALL-ORDER-EXCHANGE","order.ttl",111);
    }

}
