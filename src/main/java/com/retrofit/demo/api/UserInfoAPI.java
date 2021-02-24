package com.retrofit.demo.api;


import com.retrofit.demo.service.UserInfoService;
import com.retrofit.demo.service.responseEntity.Result;
import com.retrofit.demo.service.responseEntity.ResultEmpty;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
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
    public Object getUserInfo(@RequestParam String id, HttpServletRequest request) {
        LOGGER.info(" info for headers {}", wrapHeaders(request.getHeaderNames()));
        return userInfoService.getUserInfo(id);
    }

    private String wrapHeaders(Enumeration<String> headerNames) {
        StringBuilder s = new StringBuilder();
        while (headerNames.hasMoreElements()) {
            s.append(headerNames.nextElement()).append("||");
        }
        return s.toString();
    }

    @RequestMapping(value = "/deleteUser/{id}", method = {RequestMethod.DELETE})
    public Object deleteUser(@PathVariable String id, HttpServletRequest request) {
        LOGGER.info(" info for PathVariable {}", id);
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
        File dest = new File(filePath + "/" + fileName);
        try {
            FileUtils.copyInputStreamToFile(file.getInputStream(), dest);
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
        String filePath = System.getProperty("user.dir").concat("/workspace/temp/");
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            if (file.isEmpty()) {
                return "上传第" + (i++) + "个文件失败";
            }
            String fileName = file.getOriginalFilename();

            File dest = new File(filePath + fileName);
            try {
                FileUtils.copyInputStreamToFile(file.getInputStream(), dest);
                LOGGER.info("第" + (i + 1) + "个文件上传成功");
            } catch (IOException e) {
                LOGGER.error(e.toString(), e);
                return ResultEmpty.builder().code(9399292).msg("failure").build();
            }
        }
        return ResultEmpty.builder().code(0).msg("success").build();
    }

    @RequestMapping(value = "/download", method = {RequestMethod.POST, RequestMethod.GET})
    public Object download(@RequestParam("file") @NotEmpty String fileName, HttpServletResponse response) throws Exception {
        String filePath = System.getProperty("user.dir").concat("/workspace/temp/");
        File file = new File(filePath, fileName);
        if(!file.exists()){
            return Result.builder().code(9299399).msg("file not found").build();
        }
        response.reset();
        response.setContentType(new MimetypesFileTypeMap().getContentType(file));
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));) {
            byte[] buff = new byte[1024];
            OutputStream os  = response.getOutputStream();
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            return Result.builder().code(9299398).msg("fail download file").build();
        }
        return null;
    }
}
