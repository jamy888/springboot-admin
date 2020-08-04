package com.eyoung.springbootadmin.app.controller;

import com.eyoung.springbootadmin.weixin.mp.service.IAuthStatus;
import com.eyoung.springbootadmin.weixin.mp.service.IWeixinService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/wechat")
@Slf4j
public class WechatAuthController {

    @Autowired
    private IWeixinService weixinService;

    /**
     * pc点击微信登录，生成登录二维码
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/wxLoginPage", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> wxLoginPage(HttpServletRequest request) throws Exception {
        String sessionId = request.getSession().getId();
        log.info("sessionId:" + sessionId);
        //设置redirect_uri和state=sessionId以及测试号信息，返回授权url
        String uri = weixinService.getAuthorizationUrl("mobile", sessionId);
        log.info(uri);
        Map<String, String> map = new HashMap<String, String>();
        map.put("sessionId", sessionId);
        //用来前端生成二维码
        map.put("uri", uri);

        weixinService.putSessionStatusToCache(sessionId, IAuthStatus.INIT);
        return map;
    }

//    /**
//     * 扫描二维码授权成功，取到code，回调方法
//     *
//     * @param sessionId
//     * @param request
//     * @param response
//     * @return
//     * @throws Exception
//     */
//    @RequestMapping(value = "/pcAuth")
//    @ResponseBody
//    public String pcCallback(String sessionId, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        log.info("------------授权成功----------------");
//        WxMpUser wxMpUser = weixinService.getWxMpUser(request, response);
//        if (wxMpUser != null) {
//            log.info("-----nickname-----" + wxMpUser.getNickname());
//            log.info("-----sessionId-----" + sessionId);
//            weixinService.putSessionStatusToCache(sessionId, IAuthStatus.AUTHORIZED);
//            return "授权成功";
////            return "redirect:/auth-success";
//        }
//        return "授权失败";
////        return "redirect:/auth-fail";
//    }

    /**
     * 扫描二维码授权成功，取到code，回调方法
     *  注意：sessionId参数不能遗漏，否则易出现无法授权的问题
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/pcAuth")
    @ResponseBody
    public String pcCallback(HttpServletRequest request, HttpServletResponse response){
        return weixinService.getRedirectUrl(request, "/wechat/auth-result");
    }

    @GetMapping("/auth-result")
    public String authResult(String code, String sessionId, HttpServletRequest request, HttpServletResponse response) throws Exception  {
        WxMpUser wxMpUser = weixinService.getWxMpUser(code, sessionId);
        if (wxMpUser != null){
            weixinService.putSessionStatusToCache(sessionId, IAuthStatus.AUTHORIZED);
            return "auth-success";
        }

        return "auth-fail";
    }


    /**
     * 扫描二维码授权成功，取到code，回调方法
     *
     * @param sessionId
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/pcAuthCancelled")
    @ResponseBody
    public String pcAuthCancelled(String sessionId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("------------授权取消----------------");
        weixinService.putSessionStatusToCache(sessionId, IAuthStatus.CANCELLED);
        return "登录失败！";
    }


    @RequestMapping(value = "/polling", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> polling(String sessionId, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        log.info("进入轮询查询用户操作");
        resultMap.put("status", 200);
        if (StringUtils.isNotBlank(sessionId)){
            String authStatus = weixinService.getSessionStatusFromCache(sessionId);
            resultMap.put("authStatus", authStatus);
            if (StringUtils.isBlank(authStatus)){
                resultMap.put("message", "二维码已失效");
                log.info("sessionId : {} 对应的二维码已经失效", sessionId);
            }
            if (IAuthStatus.INIT.equalsIgnoreCase(authStatus)){
                resultMap.put("message", "二维码初始化完成");
                log.info("sessionId : {} 对应的二维码初始化完成", sessionId);
            }
            if (IAuthStatus.SCANNED.equalsIgnoreCase(authStatus)){
                resultMap.put("message", "扫码成功，请在手机上点击确认");
                log.info("sessionId : {} 对应的二维码已扫码，请在手机上点击确认", sessionId);
            }
            if (IAuthStatus.AUTHORIZED.equalsIgnoreCase(authStatus)){

                resultMap.put("message", "授权成功");
                log.info("sessionId : {} 对应的二维码已扫码且授权成功", sessionId);
            }
            if (IAuthStatus.CANCELLED.equalsIgnoreCase(authStatus)){
                resultMap.put("message", "取消授权");
                log.info("sessionId : {} 对应的二维码用户已取消授权", sessionId);
            }
        }
        return resultMap;
    }
}
