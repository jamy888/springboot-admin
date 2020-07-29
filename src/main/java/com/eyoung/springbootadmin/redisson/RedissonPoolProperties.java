package com.eyoung.springbootadmin.redisson;

import lombok.Data;

@Data
public class RedissonPoolProperties {
    /**
     * 连接池中的最大空闲连接
     */
//    @Value("${spring.redisson.pool.maxIdle}")
    private int maxIdle;
    /**
     * 最小连接数
     */
//    @Value("${spring.redisson.pool.minIdle}")
    private int minIdle;
    /**
     * 连接池最大连接数
     */
//    @Value("${spring.redisson.pool.maxActive}")
    private int maxActive;

    /**
     * 连接池最大阻塞等待时间
     */
//    @Value("${spring.redisson.pool.maxWait}")
    private int maxWait;
}
