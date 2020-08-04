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
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auth eYoung
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
     * 获取当前页面的重定向页面链接
     *
     * @param request
     * @return
     */
    @Override
    public String getRequestUrl(HttpServletRequest request, String... delParams) {
        String str = request.getScheme() + "://" + request.getServerName()
//                +":"+request.getServerPort()
                + request.getContextPath();
        String redirect_uri = StringUtils.isBlank(domainPage) ? str : domainPage;
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
     * 获取指定页面的重定向页面链接
     * @param request
     * @param servletPath
     * @param delParams
     * @return
     */
    @Override
    public String setRequestUrl(HttpServletRequest request, String servletPath, String[] delParams) {
        String str = request.getScheme() + "://" + request.getServerName()
//                +":"+request.getServerPort()
                + request.getContextPath();
        String redirect_uri = StringUtils.isBlank(domainPage) ? str : domainPage;
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
        String sessionId = request.getParameter("sessionId");
        try {
            if (StringUtils.isBlank(code)) {
                response.sendRedirect(getMpWxOauth2(getRequestUrl(request), true));
                return null;
            } else {
                WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
                WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
                if (wxMpUser != null) {
                    putWxMpUserToCache(sessionId, wxMpUser);
                }
                return wxMpUser;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public String getRedirectUrl(HttpServletRequest request, String servletPath) {
        return getMpWxOauth2(setRequestUrl(request, servletPath, null), true);
    }

    @Override
    public WxMpUser getWxMpUser(String code, String sessionId) {
        try {
            WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
            WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
            if (wxMpUser != null) {
                putWxMpUserToCache(sessionId, wxMpUser);
            }
            return wxMpUser;
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
            if (JacksonUtil.isJson(accessTokenStr)) {
                mpAccessToken = JacksonUtil.readValue(accessTokenStr, MpAccessToken.class);
            } else {
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

    @Override
    public String refreshToken(String refreshToken) throws WxErrorException {
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxMpService.oauth2refreshAccessToken(refreshToken);
        //TODO
        return wxMpOAuth2AccessToken.getAccessToken();
    }

    /**
     * pc回调域名
     */
    private String pcCallbackUrl = "http://zyy.gdtengnan.com/spring_security/wechat/pcAuth";

    /**
     * mobile回调域名
     */
    private String mobileCallbackUrl = "http://zyy.gdtengnan.com/spring_security/mobile-auth";

    /**
     * snsapi_userinfo  snsapi_base
     */
    private static final String SCOPE = "snsapi_userinfo";

    @Override
    public String getAuthorizationUrl(String type, String state) throws UnsupportedEncodingException {

        String callbackUrl = "";
        String urlState = state;
        //移动端 pc端回调方法不一样
        if ("pc".equals(type)) {
            callbackUrl = pcCallbackUrl;

        } else if ("mobile".equals(type)) {
            callbackUrl = mobileCallbackUrl;
        }
        String url = wxMpService.oauth2buildAuthorizationUrl(callbackUrl, SCOPE, urlState);
        return url;
    }

    private String getWxMpUserKey(String sessionId) {
        return redis_prefix + ":weixin:wxmpuser:" + sessionId;
    }

    private String getSessionStatus(String sessionId) {
        return redis_prefix + ":authstatus:sessionId:" + sessionId;
    }

    @Override
    public void putWxMpUserToCache(String sessionId, WxMpUser wxMpUser) {
        RBucket<WxMpUser> rBucket = redissonClient.getBucket(getWxMpUserKey(sessionId));
        rBucket.set(wxMpUser, 1, TimeUnit.HOURS);
    }

    @Override
    public WxMpUser getWxMpUserFromCache(String sessionId) {
        RBucket<WxMpUser> rBucket = redissonClient.getBucket(getWxMpUserKey(sessionId));
        return rBucket.get();
    }

    @Override
    public void putSessionStatusToCache(String sessionId, String status) {
        RBucket<String> rBucket = redissonClient.getBucket(getSessionStatus(sessionId));
        rBucket.set(status, 5, TimeUnit.MINUTES);
    }

    @Override
    public String getSessionStatusFromCache(String sessionId) {
        RBucket<String> rBucket = redissonClient.getBucket(getSessionStatus(sessionId));
        return rBucket.get();
    }
}
