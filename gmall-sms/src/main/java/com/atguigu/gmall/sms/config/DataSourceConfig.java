package com.atguigu.gmall.sms.config;

import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-07 10:27
 * @version: 1.0
 * @modified By:十一。
 */
@Configuration
public class DataSourceConfig {

    @Bean("dataSource")
    @Primary
    public DataSource getDataSource(@Value("${spring.datasource.username}")String username,@Value("${spring.datasource.password}")String password,
                                    @Value("${spring.datasource.url}")String jdbcUrl,@Value("${spring.datasource.driver-class-name}")String driverClassName){
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setUsername(username);
        hikariDataSource.setPassword(password);
        hikariDataSource.setJdbcUrl(jdbcUrl);
        hikariDataSource.setDriverClassName(driverClassName);

        return new DataSourceProxy(hikariDataSource);

    }
}
