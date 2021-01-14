package com.retrofit.demo.api;

import com.retrofit.demo.service.responseEntity.Result;
import com.retrofit.demo.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author puthlive
 */
@RestController
public class UserInfoAPI {
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


}
