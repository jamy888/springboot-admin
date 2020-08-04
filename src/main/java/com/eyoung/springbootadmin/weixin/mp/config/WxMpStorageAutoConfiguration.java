package com.eyoung.springbootadmin.weixin.mp.config;

import com.eyoung.springbootadmin.util.HttpUtil;
import com.eyoung.springbootadmin.util.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auth eYoung
 * Date: 2019/11/21
 * Time: 17:09
 * To change this template use File | Settings | File Templates.
 * Description:
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(WxMpProperties.class)
public class WxMpStorageAutoConfiguration {

    @Autowired
    private WxMpProperties properties;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 可参考 com.binarywang.spring.starter.wxjava.mp.config.WxMpStorageAutoConfiguration  修改注册 WxMpConfigStorage
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(WxMpConfigStorage.class)
    public WxMpConfigStorage wxMpInMemoryConfigStorage() {
        WxMpProperties.ConfigStorage storage = properties.getConfigStorage();
        WxMpProperties.StorageType type = storage.getType();

        if (type == WxMpProperties.StorageType.redis) {
            return getWxMpInRedissonConfigStorage();
        }
        return getWxMpDefaultConfigStorage();
    }

    private WxMpDefaultConfigImpl getWxMpDefaultConfigStorage() {

        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
        setWxMpInfo(config);
        return config;
    }

    private void setWxMpInfo(WxMpDefaultConfigImpl config) {
        config.setAppId(properties.getAppId());
        config.setSecret(properties.getSecret());
        config.setToken(properties.getToken());
        config.setAesKey(properties.getAesKey());
        if ("true".equalsIgnoreCase(properties.getAccessTokenShare())) {
            log.info("通过共享获取accessToken");
            String accessTokenStr = HttpUtil.get(properties.getAccessTokenUrl());

            if (StringUtils.isNotBlank(accessTokenStr)) {
                accessTokenStr = accessTokenStr.replace("\n", "");
            }

            MpAccessToken mpAccessToken = new MpAccessToken();
            if(JacksonUtil.isJson(accessTokenStr)){
                mpAccessToken = JacksonUtil.readValue(accessTokenStr, MpAccessToken.class);
            }else{
                mpAccessToken.setAccesstoken(accessTokenStr);
                mpAccessToken.setExpireSeconds(100 * 120);
            }
            config.setAccessToken(mpAccessToken.getAccesstoken());
            config.updateAccessToken(mpAccessToken.getAccesstoken(), (mpAccessToken.getExpireSeconds() / 2));
        }
    }

    private WxMpConfigStorage getWxMpInRedissonConfigStorage() {
        WxMpRedissonConfigImpl wxMpRedisConfig = new WxMpRedissonConfigImpl(redissonClient);
        setWxMpInfo(wxMpRedisConfig);
        return wxMpRedisConfig;
    }

}
