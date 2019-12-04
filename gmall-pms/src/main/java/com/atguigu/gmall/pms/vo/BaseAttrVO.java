package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-04 15:05
 * @version: 1.0
 * @modified By:十一。
 */
@Data
public class BaseAttrVO extends ProductAttrValueEntity {

    public void setAttrValue(List<String> valueSelected){
        if (CollectionUtils.isEmpty(valueSelected)){
            return;
        }
        this.setAttrValue(StringUtils.join(valueSelected,","));
    }
}
