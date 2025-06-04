package com.td.tdpicturebackend;

import org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

// 分库分表功能取消
// @SpringBootApplication(exclude = {ShardingSphereAutoConfiguration.class})
@SpringBootApplication
@EnableAsync
@MapperScan("com.td.tdpicturebackend.mapper")
// 设置一个代理 默认为false
@EnableAspectJAutoProxy(exposeProxy = true)
public class TdPictureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TdPictureBackendApplication.class, args);
    }

}
