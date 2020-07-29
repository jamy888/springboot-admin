package com.eyoung.springbootadmin.weixin.mp.service.impl;

import com.eyoung.springbootadmin.util.HttpUtil;
import com.eyoung.springbootadmin.util.JacksonUtil;
import com.eyoung.springbootadmin.weixin.mp.config.MpAccessToken;
import com.eyoung.springbootadmin.weixin.mp.config.WxMpProperties;
import com.eyoung.springbootadmin.weixin.mp.service.IWeixinService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zou
 * Date: 2019/11/17
 * Time: 17:10
 * To change this template use File | Settings | File Templates.
 * Description:
 */
@Slf4j
@Service
public class WeixinServiceImpl implements IWeixinService {

    @Value("${domainPage}")
    private String domainPage;
    @Value("${spring.cache.redis.key-prefix}")
    public String redis_prefix;

    @Autowired
    private WxMpService wxMpService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private WxMpProperties properties;

    private String getAccessTokenKey() {
        return redis_prefix + ":weixin:accessToken";
    }

    @Override
    public String getRequestURI(HttpServletRequest request) {
        return getRequestURI(request, domainPage);
    }

    @Override
    public String getRequestURI(HttpServletRequest request, String requestUri) {
        String str = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        String redirect_uri = StringUtils.isBlank(requestUri) ? str : requestUri;
        String servletPath = request.getServletPath();
        String requestURI = redirect_uri + servletPath;
        return requestURI;
    }

    /**
     * 获取请求地址
     *
     * @param request
     * @return
     */
    @Override
    public String getRequestUrl(HttpServletRequest request, String... delParams) {
        String str = request.getScheme() + "://" + request.getServerName()
//                +":"+request.getServerPort()
                + request.getContextPath();
        String redirect_uri = StringUtils.isBlank(domainPage)? str : domainPage;
//        redirect_uri = str;
        String servletPath = request.getServletPath();
        List<String> result = Lists.newArrayList();
        Map<String, ?> params = request.getParameterMap();

        for (String key : params.keySet()) {
            if (!ArrayUtils.contains(delParams, key)) {
                result.add(key.trim() + "=" + StringUtils.defaultIfBlank(request.getParameter(key), ""));
            }
        }
        String requestUrl = redirect_uri + servletPath;

        if (!CollectionUtils.isEmpty(result)) {
            requestUrl += "?" + StringUtils.join(result, "&");
        }
        return requestUrl;
    }

    /**
     * 是否为微信浏览器打开
     *
     * @param request
     * @return
     */
    @Override
    public boolean isWechatAccess(HttpServletRequest request) {
        boolean access = false;
        String ua = request.getHeader("user-agent");
        // 是微信浏览器
        if (ua.toLowerCase().indexOf("micromessenger") > 0) {
            access = true;
        }
        return access;
    }

    /**
     * 服务号页面二次认证
     *
     * @param redirect_uri
     * @param flag         是否弹出授权页面 true 弹出，false 不弹出
     * @return
     */
    @Override
    public String getMpWxOauth2(String redirect_uri, boolean flag) {
        String snsapi = flag ? WxConsts.OAuth2Scope.SNSAPI_USERINFO : WxConsts.OAuth2Scope.SNSAPI_BASE;
        String url = wxMpService.oauth2buildAuthorizationUrl(redirect_uri, snsapi, "STATE");
        return url;
    }


    /**
     * 服务号获取用户/粉丝的信息
     *
     * @param request
     * @param response
     * @return
     */
    @Override
    public WxMpUser getWxMpUser(HttpServletRequest request, HttpServletResponse response) {
        String code = request.getParameter("code");
        try {
            if (StringUtils.isBlank(code)) {
                response.sendRedirect(getMpWxOauth2(getRequestUrl(request), true));
                return new WxMpUser();
            } else {
                WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
                WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
                return wxMpUser;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public String getAccessToken() throws Exception {
        return getMpAccessToken().getAccesstoken();
    }

    @Override
    public MpAccessToken getMpAccessToken() throws Exception {
        MpAccessToken mpAccessToken = new MpAccessToken();
        if ("true".equalsIgnoreCase(properties.getAccessTokenShare())) {
            log.info("通过共享获取accessToken");
            String accessTokenStr = HttpUtil.get(properties.getAccessTokenUrl());
            String accessToken = accessTokenStr;
            if(JacksonUtil.isJson(accessTokenStr)){
                mpAccessToken = JacksonUtil.readValue(accessTokenStr, MpAccessToken.class);
            }else{
                mpAccessToken.setAccesstoken(accessToken);
                mpAccessToken.setExpireSeconds(100 * 120);
            }
            WxMpConfigStorage configStorage = wxMpService.getWxMpConfigStorage();
            configStorage.updateAccessToken(mpAccessToken.getAccesstoken(), (mpAccessToken.getExpireSeconds() / 2));
            wxMpService.setWxMpConfigStorage(configStorage);
            return mpAccessToken;
        }

        mpAccessToken.setAccesstoken(wxMpService.getAccessToken());
        Long expireSecond = wxMpService.getWxMpConfigStorage().getExpiresTime();
        mpAccessToken.setExpireSeconds(expireSecond.intValue());
        return mpAccessToken;
    }

    private void putAccessTokenToCache(String accessToken) throws Exception {
        RBucket<String> rBucket = redissonClient.getBucket(getAccessTokenKey());
        rBucket.set(accessToken, 100, TimeUnit.MINUTES);
    }

    private String getAccessTokenFromCache() throws Exception {
        RBucket<String> rBucket = redissonClient.getBucket(getAccessTokenKey());
        String accessTokenStr = rBucket.get();
        if (StringUtils.isBlank(accessTokenStr)) {
            return null;
        }
        return accessTokenStr;
    }

    @Override
    public WxJsapiSignature createJsapiSignature(String url) throws WxErrorException {
        WxJsapiSignature wxJsapiSignature = wxMpService.createJsapiSignature(url);
        return wxJsapiSignature;
    }

    @Override
    public String[] getCallbackIP() throws WxErrorException {
        return wxMpService.getCallbackIP();
    }

//    @Scheduled(cron = "0/5 * * * * ?")
    public void updateAccessToken() throws Exception {
        getMpAccessToken();
    }
}
