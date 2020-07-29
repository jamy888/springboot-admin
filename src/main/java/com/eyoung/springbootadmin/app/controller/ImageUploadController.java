package com.eyoung.springbootadmin.app.controller;

import com.eyoung.springbootadmin.app.entity.UploadImageResp;
import com.eyoung.springbootadmin.app.service.IImageUploadLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("")
public class ImageUploadController {

    @Value("${imageDomain}")
    private String imageDomain;
    @Autowired
    private IImageUploadLogService imageUploadLogService;

    @RequestMapping("/uploadImage")
    @ResponseBody
    public UploadImageResp uploadImage(@RequestParam(value = "upload", required = true) MultipartFile multipartFile, @RequestParam Map<String, String> params) throws Exception {
        log.info("图片上传，ticket={}，params:[{}]", params.get("ticket"), params);
//        String imgSign = params.get("imgSign");
//        if (StringUtils.isEmpty(imgSign)) {
//            throw new Exception("文件不可为空");
//        }
//        String md5sign = DigestUtils.md5DigestAsHex(multipartFile.getInputStream());
//        log.info("MD5加密后的结果结果：{}", md5sign);
//        if (!imgSign.equalsIgnoreCase(md5sign)) {
//            throw new Exception("文件校验失败");
//        }
        String fileName = imageUploadLogService.uploadImage(multipartFile);

        UploadImageResp uploadImageResp = new UploadImageResp();
//        uploadImageResp.setUploaded(1);
//        uploadImageResp.setFileName(fileName);
//        uploadImageResp.setUrl(imageDomain + "/" + fileName);
        List<String> imageUrlList = new ArrayList<>();
        imageUrlList.add(imageDomain + "/" + fileName);
        uploadImageResp.setErrno(0);
        uploadImageResp.setData(imageUrlList.toArray(new String[imageUrlList.size()]));


        return uploadImageResp;
    }

//    @RequestMapping("/upload")
//    @ResponseBody
//    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
//        if (!file.isEmpty()) {
//            try {
//                /*
//                 * 这段代码执行完毕之后，图片上传到了工程的跟路径； 大家自己扩散下思维，如果我们想把图片上传到
//                 * d:/files大家是否能实现呢？ 等等;
//                 * 这里只是简单一个例子,请自行参考，融入到实际中可能需要大家自己做一些思考，比如： 1、文件路径； 2、文件名；
//                 * 3、文件格式; 4、文件大小的限制;
//                 */
//                BufferedOutputStream out = new BufferedOutputStream(
//                        new FileOutputStream(new File(
//                                file.getOriginalFilename())));
//                System.out.println(file.getName());
//                out.write(file.getBytes());
//                out.flush();
//                out.close();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//                return "上传失败," + e.getMessage();
//            } catch (IOException e) {
//                e.printStackTrace();
//                return "上传失败," + e.getMessage();
//            }
//
//            return "上传成功";
//
//        } else {
//            return "上传失败，因为文件是空的.";
//        }
//    }

    @PostMapping(value = "/batch/upload")
    @ResponseBody
    public UploadImageResp handleFileUpload(HttpServletRequest request) throws Exception {
        MultipartHttpServletRequest params = ((MultipartHttpServletRequest) request);
        List<MultipartFile> files = ((MultipartHttpServletRequest) request)
                .getFiles("file");
        String name = params.getParameter("name");
        System.out.println("name:" + name);
        String id = params.getParameter("id");
        System.out.println("id:" + id);
        MultipartFile file = null;
        List<String> imageUrlList = new ArrayList<>();
        for (int i = 0; i < files.size(); ++i) {
            file = files.get(i);
            if (!file.isEmpty()) {
                String fileName = imageUploadLogService.uploadImage(file);
                imageUrlList.add(imageDomain + "/" + fileName);
            } else {
                return UploadImageResp.fail(imageUrlList.toArray(new String[imageUrlList.size()]), "You failed to upload " + i + " because the file was empty.");
            }
        }


        return UploadImageResp.success(imageUrlList.toArray(new String[imageUrlList.size()]));


    }
}
