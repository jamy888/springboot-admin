package com.eyoung.springbootadmin.app.service;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface IAuthService {
    public abstract String getAccessToken(String code);
    public abstract String getOpenId(String accessToken);
    public abstract String refreshToken(String code);
    public abstract String getAuthorizationUrl(String type, String state) throws UnsupportedEncodingException;
    public abstract Map<String, String> getUserInfo(String accessToken, String openId);
}
