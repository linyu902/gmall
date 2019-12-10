package com.atguigu.gmall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-10 17:34
 * @version: 1.0
 * @modified By:十一。
 */
@Configuration
public class ElasticsearchConfig {

    @Bean
    RestHighLevelClient restHighLevelClient(){
        return new RestHighLevelClient(RestClient.builder(HttpHost.create("192.168.79.128:9200")));
    }
}
