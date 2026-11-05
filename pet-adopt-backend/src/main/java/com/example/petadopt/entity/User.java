package com.example.petadopt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户表
 * 注意：user 与 MySQL 系统库同名，表名需要加反引号
 */
@Data
@TableName("`user`")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录账号 */
    private String username;

    /** 密码（明文存储，学习项目简化处理）；任何接口都不允许把密码序列化给前端 */
    @JsonIgnore
    private String password;

    /** 姓名 */
    private String name;

    /** 手机号 */
    private String phone;

    /** 地址 */
    private String address;

    /** 角色：0 普通用户，1 管理员 */
    private Integer role;

    /** 创建时间，由数据库默认值生成 */
    private LocalDateTime createTime;
}
