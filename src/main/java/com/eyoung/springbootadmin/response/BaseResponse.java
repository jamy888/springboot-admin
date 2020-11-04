package com.eyoung.springbootadmin.response;

import lombok.Data;

/**
 * @author eYoung
 * @description:
 * @date create at 2020/10/30 10:12
 */
@Data
public class BaseResponse<T> {
    private String code;
    private String msg;
    private T data;
}
