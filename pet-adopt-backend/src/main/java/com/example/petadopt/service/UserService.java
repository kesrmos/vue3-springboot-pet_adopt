package com.example.petadopt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petadopt.common.PageResult;
import com.example.petadopt.dto.LoginDTO;
import com.example.petadopt.entity.User;
import com.example.petadopt.vo.LoginVO;
import com.example.petadopt.vo.UserVO;

/**
 * 用户服务
 */
public interface UserService extends IService<User> {

    /**
     * 登录
     *
     * @param dto       用户名 + 密码
     * @param adminOnly 是否仅允许管理员登录（后台登录传 true）
     */
    LoginVO login(LoginDTO dto, boolean adminOnly);

    /**
     * 用户管理分页
     *
     * @param keyword 账号/姓名/手机号 关键字，可为空
     */
    PageResult<UserVO> pageUsers(long page, long size, String keyword);
}
