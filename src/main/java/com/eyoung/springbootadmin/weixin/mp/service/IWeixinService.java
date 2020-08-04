package com.eyoung.springbootadmin.weixin.mp.service;

import com.eyoung.springbootadmin.weixin.mp.config.MpAccessToken;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auth eYoung
 * Date: 2019/11/17
 * Time: 17:09
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public interface IWeixinService {

    String getRequestURI(HttpServletRequest request);

    String getRequestURI(HttpServletRequest request, String requestUri);

    /**
     * 获取当前页面的重定向页面链接
     * @param request
     * @param delParams
     * @return
     */
    String getRequestUrl(HttpServletRequest request, String... delParams);

    /**
     * 获取指定页面的重定向页面链接
     * @param request
     * @param servletPath
     * @param delParams
     * @return
     */
    String setRequestUrl(HttpServletRequest request, String servletPath, String[] delParams);

    boolean isWechatAccess(HttpServletRequest request);

    String getMpWxOauth2(String redirect_uri, boolean flag);

    WxMpUser getWxMpUser(HttpServletRequest request, HttpServletResponse response);

    String getAccessToken() throws Exception;

    MpAccessToken getMpAccessToken() throws Exception;

    WxJsapiSignature createJsapiSignature(String url) throws WxErrorException;

    String[] getCallbackIP() throws WxErrorException;

    String refreshToken(String refreshToken) throws WxErrorException;

    String getAuthorizationUrl(String type, String state) throws UnsupportedEncodingException;

    void putWxMpUserToCache(String sessionId, WxMpUser wxMpUser);

    WxMpUser getWxMpUserFromCache(String sessionId);

    void putSessionStatusToCache(String sessionId, String status);

    String getSessionStatusFromCache(String sessionId);

    String getRedirectUrl(HttpServletRequest request, String servletPath);

    WxMpUser getWxMpUser(String code, String sessionId);
}
