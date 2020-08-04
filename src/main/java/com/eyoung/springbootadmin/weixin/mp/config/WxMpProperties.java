package com.eyoung.springbootadmin.weixin.mp.config;

import com.eyoung.springbootadmin.redisson.RedissonProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auth eYoung
 * Date: 2019/11/19
 * Time: 12:01
 * To change this template use File | Settings | File Templates.
 * Description:
 */

@Data
@ConfigurationProperties(prefix = "wx.mp",ignoreUnknownFields = false)
//@ConfigurationProperties(PREFIX)
public class WxMpProperties {
//    public static final String PREFIX = "wx.mp";

    /**
     * 设置微信公众号的appid.
     */
    private String appId;

    /**
     * 设置微信公众号的app secret.
     */
    private String secret;

    /**
     * 设置微信公众号的token.
     */
    private String token;

    /**
     * 设置微信公众号的EncodingAESKey.
     */
    private String aesKey;

    /**
     * accessToken是否来源第三方共享
     */
    private String accessTokenShare;
    /**
     * accessToken是否来源第三方共享，如果是，需要第三方共享的链接地址
     */
    private String accessTokenUrl;

    /**
     * 存储策略, memory, redis.
     */
    private ConfigStorage configStorage = new ConfigStorage();


    @Data
    public static class ConfigStorage implements Serializable {
        private static final long serialVersionUID = 4815731027000065434L;

        private StorageType type = StorageType.memory;

        private RedissonProperties redis = new RedissonProperties();

    }

    public enum StorageType {
        /**
         * 内存.
         */
        memory,
        /**
         * redis.
         */
        redis
    }
}
