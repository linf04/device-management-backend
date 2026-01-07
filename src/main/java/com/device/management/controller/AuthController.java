//package com.device.management.controller;
//
//import com.device.management.dto.ApiResponse;
//import com.device.management.dto.LoginRequest;
//import com.device.management.dto.LoginResponse;
//import com.device.management.service.AuthService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
///**
// * 认证控制器
// */
//@Slf4j
//@RestController
//@RequestMapping("/auth")
//public class AuthController {
//
//    @Autowired
//    private AuthService authService;
//
//    /**
//     * 用户登录
//     */
//    @PostMapping("/login")
//    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
//        LoginResponse response = authService.login(loginRequest);
//        return ApiResponse.success("登录成功", response);
//    }
//
//}
