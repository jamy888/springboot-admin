package com.eyoung.springbootadmin.weixin.mp.config;

import lombok.Data;

@Data
public class MpAccessToken {
    private String accesstoken;
    private Integer expireSeconds;
}