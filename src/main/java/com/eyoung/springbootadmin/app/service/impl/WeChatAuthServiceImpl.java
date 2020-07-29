package com.eyoung.springbootadmin.app.service.impl;

import com.eyoung.springbootadmin.app.service.IWeChatAuthService;
import com.eyoung.springbootadmin.util.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WeChatAuthServiceImpl extends DefaultAuthServiceImpl implements IWeChatAuthService {

    /**
     * 请求此地址即跳转到二维码登录界面
     */
    private static final String AUTHORIZATION_URL =
            "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect";

    /**
     * 获取用户 openid 和access——toke 的 URL
     */
    private static final String ACCESSTOKEN_OPENID_URL =
            "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";

    private static final String REFRESH_TOKEN_URL =
            "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=%s&grant_type=refresh_token&refresh_token=%s";

    private static final String USER_INFO_URL =
            "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";

    private static final String APP_ID="wxb25d600f53151170";
    private static final String APP_SECRET="b2df7c1dc898c27776cdf628e726afd4";
    /**
     * snsapi_userinfo  snsapi_base
     */
    private static final String SCOPE = "snsapi_userinfo";

    /**
     * pc回调域名
     */
    private String pcCallbackUrl = "http://zyy.gdtengnan.com/spring-security/wechat/pcAuth";

    /**
     * mobile回调域名
     */
    private String mobileCallbackUrl = "https://zyy.gdtengnan.com/spring-security/wechat/mobileAuth";

    @Override
    public String getOpenId(String accessToken) {
        return null;
    }

    @Override
    public String refreshToken(String code) {
        String url = String.format(REFRESH_TOKEN_URL,APP_ID,code);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();

        ResponseEntity<Map> resp = getRestTemplate().getForEntity(uri,Map.class);
        Map<String, String> jsonObject = resp.getBody();

        String access_token = jsonObject.get("access_token");
        return access_token;
    }

    /**
     * 第一步，带着参数
     * appid：公众号的唯一标识
     * redirect_uri：授权后重定向的回调链接地址
     * response_type：返回类型，填写code
     * scope：应用授权作用域，snsapi_base / snsapi_userinfo
     * state：非必传，重定向后会带上state参数，开发者可以填写a-zA-Z0-9的参数值，最多128字节
     * wechat_redirect：无论直接打开还是做页面302重定向时候，必须带此参数
     * @param type
     * @param state
     * @return
     * @throws UnsupportedEncodingException
     */
    @Override
    public String getAuthorizationUrl(String type, String state) throws UnsupportedEncodingException {
        String callbackUrl = "";
        Object urlState = "";
        //移动端 pc端回调方法不一样
        if("pc".equals(type)){
            callbackUrl = URLEncoder.encode(pcCallbackUrl,"utf-8");
            urlState = state;
        }else if("mobile".equals(type)){
            callbackUrl = URLEncoder.encode(mobileCallbackUrl,"utf-8");
            urlState = System.currentTimeMillis();
        }
        String url = String.format(AUTHORIZATION_URL,APP_ID,callbackUrl,SCOPE,urlState);
        return url;
    }

    /** 第二步
     *  传appid  secret code grant_type=authorization_code
     *  获得 access_token openId等
     */
    @Override
    public String getAccessToken(String code) {
        String url = String.format(ACCESSTOKEN_OPENID_URL,APP_ID,APP_SECRET,code);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();

        String resp = getRestTemplate().getForObject(uri, String.class);
        log.error("getAccessToken resp = "+resp);
        if(resp.contains("openid")){
            Map<String, String> params = JacksonUtil.readValue(resp, HashMap.class);

            String access_token = params.get("access_token");
            String openId = params.get("openid");
            String refresh_token = params.get("refresh_token");

            Map<String, String> res = new HashMap<>();
            res.put("access_token",access_token);
            res.put("openId",openId);
            res.put("refresh_token",refresh_token);

            return JacksonUtil.toJson(res);
        }else{
            log.error("获取用户信息错误，msg = "+resp);
            return null;
        }
    }

    @Override
    public Map<String, String> getUserInfo(String accessToken, String openId) {
        String url = String.format(USER_INFO_URL, accessToken, openId);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();

        String resp = getRestTemplate().getForObject(uri, String.class);
        log.error("getUserInfo resp = "+resp);
        if(resp.contains("errcode")){
            log.error("获取用户信息错误，msg = "+resp);
            return null;
        }else{
            Map<String, String> data = JacksonUtil.readValue(resp, HashMap.class);

            Map<String, String> result = new HashMap<>();
            result.put("id",data.get("unionid"));
            result.put("sex",data.get("sex"));
            result.put("nickName",data.get("nickname"));
            result.put("avatar",data.get("headimgurl"));

            return result;
        }
    }
}
