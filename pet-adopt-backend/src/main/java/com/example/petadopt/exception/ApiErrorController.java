package com.example.petadopt.exception;

import com.example.petadopt.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 统一错误页：把 Spring 默认的 /error 输出（404、405、500 等）也包装成 {code, data, msg}
 * 否则未匹配的接口会返回 {timestamp,status,error,path}，前端会判定为"接口返回格式异常"
 */
@Slf4j
@RestController
public class ApiErrorController implements ErrorController {

    @RequestMapping("/error")
    public Result<Void> handleError(HttpServletRequest request, HttpServletResponse response) {
        // 与业务接口保持一致：前端统一按 code 判断，HTTP 状态码固定 200，
        // 避免 axios 因 404/500 走 HTTP 异常分支而丢掉 msg
        response.setStatus(HttpServletResponse.SC_OK);

        int status = resolveStatus(request);
        Object uri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        log.warn("请求进入错误页面: status={}, uri={}", status, uri);

        if (status == Result.NOT_FOUND) {
            return Result.error(Result.NOT_FOUND, "接口不存在" + (uri == null ? "" : ": " + uri));
        }
        if (status == Result.UNAUTHORIZED) {
            return Result.error(Result.UNAUTHORIZED, "未登录，请先登录");
        }
        if (status == Result.FORBIDDEN) {
            return Result.error(Result.FORBIDDEN, "权限不足，仅管理员可访问");
        }
        if (status == 405) {
            return Result.error(Result.BAD_REQUEST, "请求方式不支持");
        }
        return Result.error(Result.ERROR, "服务器开小差了，请稍后重试");
    }

    /** 从请求属性中取 HTTP 状态码，取不到按 500 处理 */
    private int resolveStatus(HttpServletRequest request) {
        Object value = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (value == null) {
            return Result.ERROR;
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            return Result.ERROR;
        }
    }
}