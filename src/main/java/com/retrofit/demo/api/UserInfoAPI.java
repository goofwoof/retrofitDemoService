package com.retrofit.demo.api;

import com.retrofit.demo.service.responseEntity.Result;
import com.retrofit.demo.service.UserInfoService;
import com.retrofit.demo.service.responseEntity.ResultEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * @author puthlive
 */
@RestController
public class UserInfoAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserInfoAPI.class);

    @Autowired
    private UserInfoService userInfoService;
    private static int time_error = 1;

    @RequestMapping(value = "/getUserInfo", method = {RequestMethod.POST, RequestMethod.GET})
    public Object getUserInfo(@RequestParam String id) throws Exception {
        return userInfoService.getUserInfo(id);
    }

    @RequestMapping(value = "/getUserInfoRetry", method = {RequestMethod.POST, RequestMethod.GET})
    public Object getUserInfoRetry(@RequestParam String id) throws Exception {
        if (time_error % 10 != 0){
            time_error++;
            throw new IOException("this time"+ time_error + "is retry error");
        }
        time_error=1;
        return userInfoService.getUserInfo(id);
    }

    @RequestMapping(value = "/getUserDegrade", method = {RequestMethod.POST, RequestMethod.GET})
    public Object getUserDegrade(@RequestParam String id) throws Exception {
        return userInfoService.getUserInfo(id);
    }

    @PostMapping("/upload")
    @ResponseBody
    public Object upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "上传失败，请选择文件";
        }

        String fileName = file.getOriginalFilename();
        String filePath = System.getProperty("user.dir").concat("/workspace/temp/");
        File dest = new File(new File(filePath).getAbsolutePath()+ "/" + fileName);
        try {
            file.transferTo(dest);
            LOGGER.info("上传成功");
            return ResultEmpty.builder().code(0).msg("success").build();
        } catch (IOException e) {
            LOGGER.error(e.toString(), e);
        }
        return ResultEmpty.builder().code(9399292).msg("failure").build();
    }

    @PostMapping("/multiUpload")
    @ResponseBody
    public Object multiUpload(MultipartHttpServletRequest request) {
        List<MultipartFile> files = request.getFiles("file");
        String filePath = System.getProperty("user.dir").concat("workspace/temp/");
        createDir(filePath);
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            if (file.isEmpty()) {
                return "上传第" + (i++) + "个文件失败";
            }
            String fileName = file.getOriginalFilename();

            File dest = new File(filePath + fileName);
            try {
                file.transferTo(dest);
                LOGGER.info("第" + (i + 1) + "个文件上传成功");
            } catch (IOException e) {
                LOGGER.error(e.toString(), e);
                return ResultEmpty.builder().code(9399292).msg("failure").build();
            }
        }
        return ResultEmpty.builder().code(0).msg("success").build();
    }

    private boolean createDir(String path) {
        Assert.isTrue(path.endsWith("/"), "path should be a directory");
        File file=new File(path);
        // 如果文件夹不存在
        if(!file.exists()) {
            // 创建文件夹
            return file.mkdir();
        }
        return false;
    }
}
