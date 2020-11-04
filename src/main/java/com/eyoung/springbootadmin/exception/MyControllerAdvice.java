package com.eyoung.springbootadmin.exception;

import com.eyoung.springbootadmin.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author eYoung
 * @description:
 * @date create at 2020/10/30 10:09
 */
@Slf4j
@ControllerAdvice
public class MyControllerAdvice {

    /**
     * 全局异常捕捉处理
     *
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public BaseResponse errorHandler(Exception ex, HttpServletRequest request) {
        log.error("系统异常，原因：", ex);
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode("9999");
        baseResponse.setMsg(logStackTrace(ex));
        return baseResponse;
    }

    public String logStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        log.error("系统异常，原因", e);
        String msg = sw.toString();
        return msg;
    }
}
