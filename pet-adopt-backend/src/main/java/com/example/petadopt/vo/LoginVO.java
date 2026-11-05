package com.example.petadopt.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录成功返回：token + 用户信息 + 角色
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** JWT token */
    private String token;

    /** 用户信息（不含密码） */
    private UserVO userInfo;

    /** 角色：0 普通用户，1 管理员 */
    private Integer role;
}
