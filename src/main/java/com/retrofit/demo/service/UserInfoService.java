package com.retrofit.demo.service;

import com.retrofit.demo.service.dao.User;
import com.retrofit.demo.service.responseEntity.Result;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author puthlive
 */
@Service
public class UserInfoService {
    public Result<Object> getUserInfo(String id) {
        return Result.builder().code(0).msg("success").data(User.builder().age(99).gender("male").id("0001").name("alex").build()).build();
    }
}
