package com.eyoung.springbootadmin.app.service.impl;

import com.eyoung.springbootadmin.app.entity.ImageUploadLog;
import com.eyoung.springbootadmin.app.entity.UploadImageResp;
import com.eyoung.springbootadmin.app.service.IImageUploadLogService;
import com.eyoung.springbootadmin.util.Base64ImageUtil;
import com.eyoung.springbootadmin.util.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auth eYoung
 * Date: 2019/11/20
 * Time: 10:37
 * To change this template use File | Settings | File Templates.
 * Description:
 */
@Slf4j
@Service
public class ImageUploadLogServiceImpl implements IImageUploadLogService {

    /**
     * 文件基本目录
     */
    @Value("${basePath}")
    private String basePath;
    /**
     * 上传目录
     */
    @Value("${image.path}")
    private String path;
    /**
     * 上传文件大小的最大值，单位KB
     */
    @Value("${image.maxUploadSize}")
    private Long maxUploadSize;
    /**
     * 文件压缩后的最大值，单位KB
     */
    @Value("${image.maxSize}")
    private Long maxSize;
    /**
     * 图片压缩因子，即压缩后的大小/压缩前的大小，建议小于0.9
     */
    @Value("${image.accuracy}")
    private float accuracy;
    /**
     * 允许上传图片的类型
     */
    @Value("${image.allowTypes}")
    private String allowTypes;

    @Value("${imageDomain}")
    private String imageDomain;

//    @Autowired
//    private TaskExecutor taskExecutor;

    /**
     * 图片上传
     *
     * @param imgBase64
     * @return
     * @throws Exception
     */
    @Override
    public String uploadImage(String imgBase64) throws Exception {

        ImageUploadLog imageUploadLog = new ImageUploadLog();
        //校验上传文件的大小
        checkImageSize(imgBase64, imageUploadLog);
        //校验文件类型
        checkImageBase64Suffix(imgBase64);

        imageUploadLog.setOperationTime(new Date());
        String type = imgBase64.substring(0, imgBase64.indexOf(",")+1);
        String suffix = type.substring(type.indexOf("/")+1, type.indexOf(";"));
        String name = UUID.randomUUID().toString();
        String fileName = path + "/" + name + "." +suffix;
        String path = basePath + fileName;
        path = ImageUtils.saveImage(imgBase64, path);

        if (StringUtils.isEmpty(path)) {
            log.error("图片保存失败");
            throw new Exception("图片保存失败");
        }
        if (StringUtils.isEmpty(path)) {
            commpressImage(path, imageUploadLog);
        }
        UploadImageResp uploadImageResp = new UploadImageResp();
//        uploadImageResp.setUploaded(1);
//        uploadImageResp.setFileName(fileName);
//        uploadImageResp.setUrl(imageDomain + "/" + fileName);
        String[] data = {""};
        data[0] = imageDomain + "/" + fileName;
        uploadImageResp.setErrno(0);
        uploadImageResp.setData(data);
        return fileName;
    }

    /**
     * 验证图片大小
     *
     * @param imgBase64
     * @return
     * @throws Exception
     */
    private boolean checkImageSize(String imgBase64, ImageUploadLog imageUploadLog) throws Exception {
        imgBase64 = imgBase64.split(",")[1];
        int strLength = imgBase64.length();
        int size = strLength - (strLength / 8) * 2;
        log.info("###############################fileLength=" + size);
        long allowMaxSize = 1024 * maxUploadSize;
        if (size > allowMaxSize) {
            log.info("上传的文件大小为" + size + "bytes，超过最大" + allowMaxSize + "bytes的限制");
            throw new Exception("文件超出大小限制，最大限制为" + maxUploadSize + "kb，最大限制为" + maxUploadSize + "kb，上传文件的大小为" + (size / 1024) + "kb");
        }
        imageUploadLog.setFileSize(size / 1024d);
        return true;
    }

    private boolean checkImageSize(MultipartFile file, ImageUploadLog imageUploadLog) throws Exception {
        long size = file.getSize();
        long allowMaxSize = 1024 * maxUploadSize;
        if (size > allowMaxSize) {
            log.info("上传的文件大小为" + size + "bytes，超过最大" + allowMaxSize + "bytes的限制");
            throw new Exception("文件超出大小限制，最大限制为" + maxUploadSize + "kb，上传文件的大小为" + (size / 1024) + "kb");
        }

        imageUploadLog.setFileSize(size / 1024d);
        return true;
    }

    /**
     * 验证图片类型
     *
     * @param imgBase64
     * @return
     */
    private boolean checkImageBase64Suffix(String imgBase64) throws Exception {
        if (StringUtils.isEmpty(allowTypes)) {
            return true;
        }
        String suffix = imgBase64.substring(0, imgBase64.indexOf(";")).replace("data:image/", "");
        if (!allowTypes.toLowerCase().contains(suffix)) {
            throw new Exception("上传的图片类型只允许为" + allowTypes);
        }
        return true;
    }

    /**
     * 图片压缩，采用线程异步执行图片压缩
     *
     * @param path
     * @throws Exception
     */
    private void commpressImage(String path, ImageUploadLog imageUploadLog) throws Exception {
//        taskExecutor.execute(() -> {
//            log.info("当前运行的线程名称：" + Thread.currentThread().getName());
//            long start = System.currentTimeMillis();
//            Base64ImageUtil.commpressPicForScale(path, path, maxSize, accuracy);
//            long time = System.currentTimeMillis() - start;
//            log.info("耗时：" + time + "ms");
//
//            File srcFile = new File(path);
//            double srcFilesize = srcFile.length();
//            double size = srcFilesize / 1024;
//            imageUploadLog.setFileName(path);
//            imageUploadLog.setCompressTime((int) time);
//            imageUploadLog.setFileLastSize(size);
//            //保存上传图片调用日志
//            //RQueue<ImageUploadLog> queue = redissonClient.getQueue(getImageUploadLogKey());
//            //queue.add(imageUploadLog);
//        });
    }

    @Override
    public String uploadImage(MultipartFile file) throws Exception {
        if (file == null || StringUtils.isEmpty(file.getName())) {
            throw new Exception("文件不可为空");
        }
        ImageUploadLog imageUploadLog = new ImageUploadLog();
        imageUploadLog.setOperationTime(new Date());

        String[] name = file.getContentType().split("/");
        String suffix = name[name.length - 1];
        if (!allowTypes.toLowerCase().contains(suffix)) {
            throw new Exception("上传的图片类型只允许为" + allowTypes);
        }
        // 验证图片大小
        checkImageSize(file, imageUploadLog);
        // 物理地址
        String fileName = path + UUID.randomUUID().toString() + "." + suffix;
        String path = basePath + fileName;
        ImageUtils.saveImage(file, path);
        if (!StringUtils.isEmpty(fileName)) {
            commpressImage(fileName, imageUploadLog);
        }

        log.info("保存图片成功，路径加密前为：{}", path);
//        coverImagePath = EncryptDecryptUtils.doEncryptEncode(coverImagePath);
        log.info("保存图片成功，路径加密后为：{}", path);

        UploadImageResp uploadImageResp = new UploadImageResp();
//        uploadImageResp.setUploaded(1);
//        uploadImageResp.setFileName(fileName);
//        uploadImageResp.setUrl(imageDomain + "/" + fileName);
        String[] data = {""};
        data[0] = imageDomain + "/" + fileName;
        uploadImageResp.setErrno(0);
        uploadImageResp.setData(data);

        return fileName;
    }
}
