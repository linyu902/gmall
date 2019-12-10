package com.atguigu.gmall.search.repository;

import com.atguigu.gmall.search.pojo.GoodsVO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-10 11:42
 * @version: 1.0
 * @modified By:十一。
 */
public interface SearchRepository extends ElasticsearchRepository<GoodsVO,Long> {

}
