package com.atguigu.gmall.index.service;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;

import java.util.List;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-12 14:51
 * @version: 1.0
 * @modified By:十一。
 */
public interface IndexService {
    List<CategoryEntity> queryLevel1Category();

    List<CategoryVO> querySubCatrgory(Long pid);
}
