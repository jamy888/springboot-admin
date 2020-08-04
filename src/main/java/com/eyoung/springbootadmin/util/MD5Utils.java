package com.tnnet.gdfy.common.util;

//import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具
 *
 * @author zou
 * @email
 * @url tnnet.com
 * @date 2017年8月8日 下午5:17:34
 */
//@Slf4j
public class MD5Utils {


    public static String strTo16(String sourceStr) {
        String result = strTo32(sourceStr).substring(8, 24);
        //输出16位16进制字符串
        System.out.println("MD5(" + sourceStr + ",16) = " + result);
        return result;
    }

    public static String strTo32(String sourceStr) {
        //通过result返回结果值
        String result = "";
        try {
            //1.初始化MessageDigest信息摘要对象,并指定为MD5不分大小写都可以
            MessageDigest md = MessageDigest.getInstance("MD5");
            //2.传入需要计算的字符串更新摘要信息，传入的为字节数组byte[],将字符串转换为字节数组使用getBytes()方法完成
            md.update(sourceStr.getBytes());
            //3.计算信息摘要digest()方法,返回值为字节数组
            byte b[] = md.digest();

            //定义整型
            int i;
            //声明StringBuffer对象
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                //将首个元素赋值给i
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    //前面补0
                    buf.append("0");
                }
                //转换成16进制编码
                buf.append(Integer.toHexString(i));
            }
            //转换成字符串
            result = buf.toString();
            //输出32位16进制字符串
           // log.debug("MD5(" + sourceStr + ",32) = " + result);
        } catch (NoSuchAlgorithmException e) {
           // log.error(e.getMessage(), e);
        }
        //返回结果
        return result;
    }

}
