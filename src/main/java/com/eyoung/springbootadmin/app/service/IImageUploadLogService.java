package com.eyoung.springbootadmin.app.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auth eYoung
 * Date: 2019/11/20
 * Time: 10:36
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public interface IImageUploadLogService {

    String uploadImage(String imgBase64) throws Exception;

    String uploadImage(MultipartFile file) throws Exception;
}
