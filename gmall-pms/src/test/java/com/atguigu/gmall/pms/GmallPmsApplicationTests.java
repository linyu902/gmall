package com.atguigu.gmall.pms;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.atguigu.gmall.pms.service.ProductAttrValueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GmallPmsApplicationTests {

    @Autowired
    ProductAttrValueService attrValueService;
    @Test
    void contextLoads() {
        List<ProductAttrValueEntity> attrValueEntities = attrValueService.querySearchAttrBySpuId(26L);
        attrValueEntities.forEach(System.out::println);
    }

}
