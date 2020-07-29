package com.eyoung.springbootadmin.util;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.thymeleaf.util.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;

@Slf4j
public class Base64ImageUtil {


    /**
     * 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
     *
     * @param imageFile
     * @return
     */
    public static String getBase64ImageContent(String imageFile) {
        InputStream in = null;
        byte[] data = null;
        // 读取图片字节数组
        try {
            in = new FileInputStream(imageFile);
            data = new byte[in.available()];
            in.read(data);
            BASE64Encoder encoder = new BASE64Encoder();
            return encoder.encode(data);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * 将图片base64字符串信息转换成文件
     *
     * @param base64Content
     * @param saveFile      文件存储目标目录，包括文件名
     * @return
     */
    public static boolean saveBase64ImageContent(String base64Content, String saveFile) {
        if (StringUtils.isEmpty(base64Content) || StringUtils.isEmpty(saveFile)) {
            return false;
        }
        OutputStream out = null;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            // Base64解码
            byte[] b = decoder.decodeBuffer(base64Content);
            for (int i = 0; i < b.length; ++i) {
                // 调整异常数据
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            // 生成jpeg图片
            File file = new File(saveFile);

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            out = new FileOutputStream(saveFile);
            out.write(b);
            out.flush();

            return true;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return false;
        } finally {
            IOUtils.closeQuietly(out);
        }
    }


    /**
     * @param srcPath     原图片地址
     * @param desPath     目标图片地址
     * @param desFileSize 指定图片大小,单位kb
     * @param accuracy    精度,递归压缩的比率,建议小于0.9
     * @return
     */
    public static String commpressPicForScale(String srcPath, String desPath,
                                              long desFileSize, double accuracy) {
        try {
            File srcFile = new File(srcPath);
            long srcFilesize = srcFile.length();
            log.info("原图片:" + srcPath + ",大小:" + srcFilesize/1024 + "kb");
            //递归压缩,直到目标文件大小小于desFileSize
            long start = System.currentTimeMillis();
            commpressPicCycle(desPath, desFileSize, accuracy);
            File desFile = new File(desPath);
            log.info("目标图片:" + desPath + ",大小" + desFile.length() / 1024 + "kb，耗时" + (System.currentTimeMillis() - start) + "ms");
            log.info("图片压缩完成!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return desPath;
    }

    public static void commpressPicCycle(String desPath, long desFileSize,
                                         double accuracy) throws IOException {
        File imgFile = new File(desPath);
        long fileSize = imgFile.length();
        //判断大小,如果小于desFileSize * 1024,不压缩,如果大于等于desFileSize * 1024,压缩
        if (fileSize <= desFileSize * 1024 ) {
            return;
        }
        //计算宽高
        BufferedImage bim = ImageIO.read(imgFile);
        int imgWidth = bim.getWidth();
        int imgHeight = bim.getHeight();
        int desWidth = new BigDecimal(imgWidth).multiply(
                new BigDecimal(accuracy)).intValue();
        int desHeight = new BigDecimal(imgHeight).multiply(
                new BigDecimal(accuracy)).intValue();
        Thumbnails.of(desPath).size(desWidth, desHeight).outputQuality(accuracy).toFile(desPath);
        //如果不满足要求,递归直至满足小于1M的要求
        commpressPicCycle(desPath, desFileSize, accuracy);
    }

    public static void commpressPicPPI(String desPath, int ppi) throws IOException {

        Thumbnails.of(desPath).size(ppi, ppi).toFile(desPath);
    }

//    public static void main(String[] args) {
//        String srcPath = "/Users/zou/Desktop/aomen.png";
//        String desPath = "/Users/zou/Desktop/aomen.png";
//        commpressPicForScale(srcPath, desPath, 3072, 0.9);
//    }

}