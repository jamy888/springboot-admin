package com.eyoung.springbootadmin.app.entity;

import lombok.Data;

@Data
public class UploadImageResp {

//    private Integer uploaded;
//
//    private String fileName;
//
//    private String url;
//
    private String errorMsg;

    private Integer errno;

    private String[] data;

    public static UploadImageResp success(String[] data){
        UploadImageResp uploadImageResp = new UploadImageResp();
        uploadImageResp.setErrno(0);
        uploadImageResp.setData(data);
        uploadImageResp.setErrorMsg("");
        return uploadImageResp;
    }

    public static UploadImageResp fail(String[] data, String errorMsg){
        UploadImageResp uploadImageResp = new UploadImageResp();
        uploadImageResp.setErrno(-1);
        uploadImageResp.setData(data);
        uploadImageResp.setErrorMsg(errorMsg);
        return uploadImageResp;
    }


}
