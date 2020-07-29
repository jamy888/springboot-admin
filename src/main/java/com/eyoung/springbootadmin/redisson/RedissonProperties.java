package com.eyoung.springbootadmin.redisson;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by mic on 2019/7/23.
 */
@ConfigurationProperties(prefix = "spring.redisson",ignoreUnknownFields = true)
@Data
@Component
public class RedissonProperties {

//    @Value("${spring.redisson.host}")
    private String host;
//    @Value("${spring.redisson.port}")
    private String port;
//    @Value("${spring.redisson.database}")
    private int database;
//    @Value("${spring.redisson.pool}")
    private RedissonPoolProperties pool;

//    private int maxRedirects;
    /**
     * 等待节点回复命令的时间。该时间从命令发送成功时开始计时
     */
    private int timeout;

    /**
     * 连接密码
     */
    private String password;

//    private Cluster cluster;
//
//    @Data
//    public static class Cluster {
//        private List<String> nodes;
//
//        private int maxRedirects;
//    }
}
