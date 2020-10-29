package com.eyoung.springbootadmin.security.core.login;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author eYoung
 * @description:
 * @date create at 2020/10/28 18:23
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChooseWay {
    // 对应的登录方式
    String value();
}
