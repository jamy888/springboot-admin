package com.eyoung.springbootadmin.app.controller;

import com.eyoung.springbootadmin.app.service.impl.WeChatAuthServiceImpl;
import com.eyoung.springbootadmin.util.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    private WeChatAuthServiceImpl weChatAuthService;


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
        String uri = weChatAuthService.getAuthorizationUrl("pc", sessionId);
        log.info(uri);
        Map<String, String> map = new HashMap<String, String>();
        map.put("sessionId", sessionId);
        map.put("uri", uri);//用来前端生成二维码
        return map;
    }

    /**
     * 扫描二维码授权成功，取到code，回调方法
     *
     * @param code
     * @param state
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/pcAuth")
    @ResponseBody
    public String pcCallback(String code, String state, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //根据code获取access_token和openId，不懂看微信文档
        String result = weChatAuthService.getAccessToken(code);
        Map<String, String> jsonObject = JacksonUtil.readValue(result, HashMap.class);
        String access_token = jsonObject.get("access_token");
        String openId = jsonObject.get("openId");

        log.info("------------授权成功----------------");
        //根据token和openId获取微信用户信息，不懂看我上一篇文章开始分享的链接
        Map<String, String> infoJson = weChatAuthService.getUserInfo(access_token, openId);
        if (infoJson != null) {
            String nickname = infoJson.get("nickName");
            log.info("-----nickname-----" + nickname);
            log.info("-----sessionId-----" + state);
            infoJson.put("openId", openId);
//            redisTemplate.opsForValue().set(state, infoJson, 10*60, TimeUnit.SECONDS);
            return "登录成功！";
        }
        return "登录失败！";
    }


    @RequestMapping(value = "/polling", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> polling(String sessionId, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        log.info("进入轮询查询用户是否已登录");
        resultMap.put("status", 0);
        return resultMap;
//        if(redisTemplate.hasKey(sessionId)){
//            JSONObject infoJson = (JSONObject)redisTemplate.opsForValue().get(sessionId);
//            redisTemplate.opsForValue().getOperations().delete(sessionId);
//            String openId = (String)infoJson.get("openId");
//            //根据openId判断我们网站是否存在该用户，数据库用户表会保存用户
//            User user = userService.selectUserByWechat(openId);
//            if (user == null) {
//                String nickname = (String)infoJson.get("nickName");
//                String sex = (String)infoJson.get("sex");
//                User newuser = new User();
//                newuser.setSex(sex);
//                newuser.setWechat(openId);
//                newuser.setNickname(nickname);
//                int i = userService.insertUser(newuser);//新增用户
//                if(i<1){
//                    resultMap.put("status", 500);
//                    resultMap.put("message", "登录失败:");
//                    return resultMap;
//                }
//            }
//            //登录操作
//            try {
//                UsernamePasswordToken token = new UsernamePasswordToken(openId, openId);//这里是用shiro登录，反正该openId已经微信扫码验证
//                SecurityUtils.getSubject().login(token);
//                resultMap.put("status", 200);
//                resultMap.put("message", "登录成功");
//
//                //更新用户最后登录时间
//                Subject  currentUser = SecurityUtils.getSubject();
//                User luser = (User) currentUser.getPrincipal();
//                User user1 = new User();
//                user1.setId(luser.getId());
//                user1.setLastLogDate(new Date());
//                userService.updateUserByIdSelective(user1);//更新用户方法
//
//
//            } catch (Exception e) {
//                resultMap.put("message", "未知系统错误:" + e.getMessage());
//            }
//            return resultMap;
//        }else{//not has key
//            resultMap.put("status", 0);
//            return resultMap;
//        }
    }
}
