package com.example.petadopt.controller;

import com.example.petadopt.common.Result;
import com.example.petadopt.dto.LoginDTO;
import com.example.petadopt.service.UserService;
import com.example.petadopt.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 用户前台：登录
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * POST /api/user/login  { "username": "zhangsan", "password": "123456" }
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO dto) {
        return Result.ok("登录成功", userService.login(dto, false));
    }
}
