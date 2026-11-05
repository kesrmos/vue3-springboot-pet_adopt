package com.example.petadopt.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果
 * 返回格式固定：{code, data, msg}
 * code：200 成功、400 参数/业务校验失败、401 未认证、403 权限不足、404 资源不存在、500 服务异常
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int SUCCESS = 200;
    /** 参数不合法 / 业务校验失败 */
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    /** 资源不存在，如宠物已被删除 */
    public static final int NOT_FOUND = 404;
    public static final int ERROR = 500;

    /** 状态码 */
    private Integer code;
    /** 数据 */
    private T data;
    /** 提示信息 */
    private String msg;

    public Result() {
    }

    public Result(Integer code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    /** 成功，无数据 */
    public static <T> Result<T> ok() {
        return new Result<>(SUCCESS, null, "操作成功");
    }

    /** 成功，带数据 */
    public static <T> Result<T> ok(T data) {
        return new Result<>(SUCCESS, data, "success");
    }

    /** 成功，自定义提示 + 数据 */
    public static <T> Result<T> ok(String msg, T data) {
        return new Result<>(SUCCESS, data, msg);
    }

    /** 失败，默认 500 */
    public static <T> Result<T> error(String msg) {
        return new Result<>(ERROR, null, msg);
    }

    /** 失败，自定义状态码 */
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, null, msg);
    }
}
