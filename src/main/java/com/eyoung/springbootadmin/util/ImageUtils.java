package com.eyoung.springbootadmin.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.io.File;
import java.util.UUID;

@Data
@Slf4j
public class ImageUtils {

    private static final String STORE_PREFIX = "/upload/image/";

    /**
     * 最大值5MB
     */
    private static final Long MAX_SIZE = 1024 * 1024 * 5L;

    /**
     * @param data
     * @return
     */
    public static String saveImage(byte[] data, String path) throws Exception {
        if (data == null || data.length == 0) {
            return "";
        }
        return saveImage(new String(data), path);
    }

    public static String saveImage(String base64Content, String fileName) throws Exception {

        if (StringUtils.isEmpty(base64Content)) {
            return "";
        }
        String type = base64Content.substring(0, base64Content.indexOf(",")+1);
        String suffix = type.substring(type.indexOf("/")+1, type.indexOf(";"));
        base64Content = base64Content.replaceAll(type, "");
        //文件重命名，采用UUID生成随机字符串
        String name = UUID.randomUUID().toString();
//        String fileName = path + "/" + name + "." +suffix;
        // 如果不存在则创建文件夹
        FileUtil.makeDirectory(fileName);

        boolean flag = Base64ImageUtil.saveBase64ImageContent(base64Content, fileName);
//        if (flag){
//            FileUtil.setPosixFilePermissions(fileName);
//        }
        return flag ? fileName : "";

    }

    public static String saveImage(MultipartFile file, String fileName) throws Exception{
        String[] name = file.getContentType().split("/");
        String suffix = name[name.length - 1];
//        String fileName = path + UUID.randomUUID().toString() + "." + suffix;
        FileUtil.makeDirectory(fileName);
        file.transferTo(new File(fileName));
//        FileUtil.setPosixFilePermissions(fileName);
        return fileName;
    }
}
