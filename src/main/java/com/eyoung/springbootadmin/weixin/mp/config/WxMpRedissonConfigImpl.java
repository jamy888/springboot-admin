package com.eyoung.springbootadmin.weixin.mp.config;

import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import me.chanjar.weixin.mp.enums.TicketType;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

public class WxMpRedissonConfigImpl extends WxMpDefaultConfigImpl {

    private static final String ACCESS_TOKEN_KEY = "wx:access_token:";

    private final RedissonClient redissonClient;

    private String accessTokenKey;

    public WxMpRedissonConfigImpl(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 每个公众号生成独有的存储key.
     */
    @Override
    public void setAppId(String appId) {
        super.setAppId(appId);
        this.accessTokenKey = ACCESS_TOKEN_KEY.concat(appId);
    }

    private String getTicketRedisKey(TicketType type) {
        return String.format("wx:ticket:key:%s:%s", this.appId, type.getCode());
    }

    @Override
    public String getAccessToken() {
        RBucket<String> rBucket = redissonClient.getBucket(this.accessTokenKey);
        return rBucket.get();
    }

    @Override
    public boolean isAccessTokenExpired() {
        RBucket<String> rBucket = redissonClient.getBucket(this.accessTokenKey);
        return rBucket.remainTimeToLive() < 20000;
    }

    @Override
    public synchronized void updateAccessToken(String accessToken, int expiresInSeconds) {
        RBucket<String> rBucket = redissonClient.getBucket(this.accessTokenKey);
        rBucket.set(accessToken,expiresInSeconds - 200, TimeUnit.SECONDS);
    }

    @Override
    public void expireAccessToken() {
        RBucket<String> rBucket = redissonClient.getBucket(this.accessTokenKey);
        rBucket.expire(0, TimeUnit.SECONDS);
    }

    @Override
    public long getExpiresTime(){
        RBucket<String> rBucket = redissonClient.getBucket(this.accessTokenKey);
        return rBucket.remainTimeToLive();
    }

    @Override
    public String getTicket(TicketType type) {
        RBucket<String> rBucket = redissonClient.getBucket(this.getTicketRedisKey(type));
        return rBucket.get();
    }

    @Override
    public boolean isTicketExpired(TicketType type) {
        RBucket<String> rBucket = redissonClient.getBucket(this.getTicketRedisKey(type));
        return rBucket.remainTimeToLive() < 20000;
    }

    @Override
    public synchronized void updateTicket(TicketType type, String jsapiTicket, int expiresInSeconds) {
        RBucket<String> rBucket = redissonClient.getBucket(this.getTicketRedisKey(type));
        rBucket.set(jsapiTicket, expiresInSeconds - 200, TimeUnit.SECONDS);
    }

    @Override
    public void expireTicket(TicketType type) {
        RBucket<String> rBucket = redissonClient.getBucket(this.getTicketRedisKey(type));
        rBucket.expire(0, TimeUnit.SECONDS);
    }
}
