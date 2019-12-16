package com.atguigu.gmall.message;

import com.atguigu.gmall.message.template.SmsTempLate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GmallMessageApplicationTests {

    @Test
    public void contextLoads() {
        SmsTempLate smsTempLate = new SmsTempLate();
        System.out.println(smsTempLate.sendMesage("17335715993", "HELLO"));
    }

}
