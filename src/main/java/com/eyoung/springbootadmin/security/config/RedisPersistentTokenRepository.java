package com.eyoung.springbootadmin.security.config;

import com.eyoung.springbootadmin.util.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/** 存储 remember-me tokend信息
 * @author eYoung
 * @description:
 * @date create at 2020/11/2 15:26
 */
@Slf4j
@Component
public class RedisPersistentTokenRepository implements PersistentTokenRepository {

    @Value("${spring.cache.redis.key-prefix}")
    public String redis_prefix;

    @Autowired
    private RedissonClient redissonClient;

    private String getKey(String seriesId){
        return redis_prefix + ":remember_me:series:" + seriesId;
    }

    private String getTokenKey(String username){
        return redis_prefix + ":remember_me:username:" + username;
    }

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        RBucket<String> tokenBucket = redissonClient.getBucket(getKey(token.getSeries()));

        tokenBucket.set(JacksonUtil.toJson(token), 7, TimeUnit.DAYS);

        RBucket<String> usernameRBucket = redissonClient.getBucket(getTokenKey(token.getUsername()));
        usernameRBucket.set(token.getSeries(), 7, TimeUnit.DAYS);
    }

    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        PersistentRememberMeToken token = getTokenForSeries(series);
        PersistentRememberMeToken newToken = new PersistentRememberMeToken(token.getUsername(), series, tokenValue, lastUsed);
        createNewToken(newToken);
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        RBucket<String> rBucket = redissonClient.getBucket(getKey(seriesId));
        String tokenJsonStr = rBucket.get();
        return StringUtils.isBlank(tokenJsonStr) ? null : JacksonUtil.readValue(tokenJsonStr, PersistentRememberMeToken.class);
    }

    @Override
    public void removeUserTokens(String username) {
        if (log.isDebugEnabled()){
            log.debug("token remove username: [{}]", username);
        }
        RBucket<String> usernameRBucket = redissonClient.getBucket(getTokenKey(username));
        String seriesId = usernameRBucket.get();
        if (StringUtils.isBlank(seriesId)){
            return;
        }
        if (log.isDebugEnabled()){
            log.debug("token remove username: [{}] match seriesId: [{}]", username, seriesId);
        }
        RBucket<String> tokenBucket = redissonClient.getBucket(getKey(seriesId));
        tokenBucket.delete();
        usernameRBucket.delete();
    }
}
