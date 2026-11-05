package com.example.petadopt.exception;

import com.example.petadopt.common.Result;
import lombok.Getter;

/**
 * 业务异常：由 GlobalExceptionHandler 统一捕获并返回 {code, msg}
 */
@Getter
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final Integer code;

    /** 默认 400：可预期的业务校验失败，不再占用 500 */
    public BizException(String msg) {
        super(msg);
        this.code = Result.BAD_REQUEST;
    }

    public BizException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }
}
