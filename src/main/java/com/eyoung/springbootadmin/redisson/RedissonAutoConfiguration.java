package com.eyoung.springbootadmin.redisson;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.spring.session.config.EnableRedissonHttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Created by mic on 2019/7/23.
 */

@Configuration
@EnableConfigurationProperties(RedissonProperties.class)
@EnableRedissonHttpSession
@ConditionalOnClass({Redisson.class})
public class RedissonAutoConfiguration {

    @Autowired
    private RedissonProperties redissonProperties;

    @Bean
    public RedissonClient redissonClient() {
        Config config = getSingleServerConfig();
        return Redisson.create(config);
    }

    private Config getSingleServerConfig(){
        Config config = new Config();
        String host = redissonProperties.getHost();
        String port = redissonProperties.getPort();
        String node = host + ":" +port;
        String password = redissonProperties.getPassword();
        node = node.startsWith("redis://") ? node : "redis://" + node;
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(node)
                .setPassword(password)
                .setDatabase(redissonProperties.getDatabase())
                .setTimeout(redissonProperties.getTimeout())
                .setConnectionMinimumIdleSize(redissonProperties.getPool().getMinIdle());
        if (StringUtils.isNotBlank(redissonProperties.getPassword())) {
            serverConfig.setPassword(redissonProperties.getPassword());
        }
        return config;
    }

//    private Config getClusterServersConfig(){
//        Config config = new Config();
//        //redisson版本是3.5，集群的ip前面要加上“redis://”，不然会报错，3.2版本可不加
//        List<String> clusterNodes = new ArrayList<>();
//        for (int i = 0; i < redissonProperties.getCluster().getNodes().size(); i++) {
//            clusterNodes.add("redis://" + redissonProperties.getCluster().getNodes().get(i));
//        }
//        config.useClusterServers()
//                //添加节点地址
//                .addNodeAddress(clusterNodes.toArray(new String[clusterNodes.size()]))
//                // 密码
//                //.setPassword(redissonProperties.getPassword())
//                //命令等待超时，单位：毫秒
//                .setTimeout(redissonProperties.getTimeout())
//                //对Redis集群节点状态扫描的时间间隔。单位是毫秒，默认值1000
//                .setScanInterval(2000)
//                .setRetryAttempts(10)
//                ;
//        return config;
//    }

}
