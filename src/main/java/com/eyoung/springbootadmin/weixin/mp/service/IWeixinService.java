package com.eyoung.springbootadmin.weixin.mp.service;

import com.eyoung.springbootadmin.weixin.mp.config.MpAccessToken;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zou
 * Date: 2019/11/17
 * Time: 17:09
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public interface IWeixinService {

    String getRequestURI(HttpServletRequest request);

    String getRequestURI(HttpServletRequest request, String requestUri);

    String getRequestUrl(HttpServletRequest request, String... delParams);

    boolean isWechatAccess(HttpServletRequest request);

    String getMpWxOauth2(String redirect_uri, boolean flag);

    WxMpUser getWxMpUser(HttpServletRequest request, HttpServletResponse response);

    String getAccessToken() throws Exception;

    MpAccessToken getMpAccessToken() throws Exception;

    WxJsapiSignature createJsapiSignature(String url) throws WxErrorException;

    String[] getCallbackIP() throws WxErrorException;
}
