package com.eyoung.springbootadmin.app.entity;

import java.io.Serializable;
import java.util.Date;

public class ImageUploadLog implements Serializable {
    private Integer id;

    private String name;

    private Date operationTime;

    private String fileName;

    private Double fileSize;

    private Integer compressTime;

    private Double fileLastSize;

    private Date createTime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Date getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(Date operationTime) {
        this.operationTime = operationTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null ? null : fileName.trim();
    }

    public Double getFileSize() {
        return fileSize;
    }

    public void setFileSize(Double fileSize) {
        this.fileSize = fileSize;
    }

    public Integer getCompressTime() {
        return compressTime;
    }

    public void setCompressTime(Integer compressTime) {
        this.compressTime = compressTime;
    }

    public Double getFileLastSize() {
        return fileLastSize;
    }

    public void setFileLastSize(Double fileLastSize) {
        this.fileLastSize = fileLastSize;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}