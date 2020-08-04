package com.eyoung.springbootadmin.weixin.mp.service;

public interface IAuthStatus {
    /**
     * 初始化
     */
    public final String INIT = "init";
    /**
     * 已扫码
     */
    final String SCANNED = "scanned";
    /**
     * 授权
     */
    final String AUTHORIZED = "authorized";
    /**
     * 取消
     */
    final String CANCELLED = "cancelled";

}
