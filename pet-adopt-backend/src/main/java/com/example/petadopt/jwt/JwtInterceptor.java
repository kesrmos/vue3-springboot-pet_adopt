package com.example.petadopt.jwt;

import com.example.petadopt.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * JWT 登录校验拦截器
 * 1. 白名单接口直接放行（登录、宠物浏览）
 * 2. 其余接口必须携带有效 token，否则 code=401
 * 3. /admin/** 需要 role=1，否则 code=403
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    /** 完全放行的接口 */
    private static final Set<String> WHITE_LIST = new HashSet<>(Arrays.asList(
            "/user/login",
            "/admin/login"
    ));

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 非 Controller 方法（静态资源、错误页）直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        // 跨域预检请求放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 去掉 context-path（/api）与末尾多余的 /，得到纯业务路径，如 /apply/my
        String path = normalizePath(request);

        // 白名单：登录接口
        if (WHITE_LIST.contains(path)) {
            return true;
        }
        // 宠物列表 / 详情允许游客浏览
        if ("GET".equalsIgnoreCase(request.getMethod())
                && ("/pet/list".equals(path) || path.matches("^/pet/\\d+$"))) {
            return true;
        }

        String token = resolveToken(request);
        if (!StringUtils.hasText(token)) {
            return reject(response, Result.UNAUTHORIZED, "未登录，请先登录");
        }

        Long userId;
        String username;
        Integer role;
        try {
            Claims claims = jwtUtil.parseToken(token);
            userId = jwtUtil.getUserId(claims);
            username = jwtUtil.getUsername(claims);
            role = jwtUtil.getRole(claims);
        } catch (ExpiredJwtException e) {
            return reject(response, Result.UNAUTHORIZED, "登录已过期，请重新登录");
        } catch (Exception e) {
            // 签名错误、格式错误、payload 缺字段等一律按未认证处理，不抛到容器层变成 500
            log.warn("token 解析失败: {}", e.getMessage());
            return reject(response, Result.UNAUTHORIZED, "登录凭证无效，请重新登录");
        }
        if (userId == null || role == null) {
            return reject(response, Result.UNAUTHORIZED, "登录凭证无效，请重新登录");
        }

        UserContext.set(new UserContext.LoginUser(userId, username, role));

        // 管理员接口权限校验
        if (path.startsWith("/admin") && !Integer.valueOf(1).equals(role)) {
            return reject(response, Result.FORBIDDEN, "权限不足，仅管理员可访问");
        }
        return true;
    }

    /**
     * 归一化请求路径：去掉 context-path（/api）以及末尾多余的 '/'
     * 例如 /api/pet/list/ -> /pet/list，避免白名单因多一个斜杠而失效
     */
    private String normalizePath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = uri;
        if (StringUtils.hasText(contextPath) && uri.startsWith(contextPath)) {
            path = uri.substring(contextPath.length());
        }
        if (!StringUtils.hasText(path)) {
            path = "/";
        }
        while (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 防止线程复用导致数据串号
        UserContext.clear();
    }

    /**
     * 支持三种写法：
     * Authorization: Bearer xxx
     * Authorization: xxx
     * token: xxx
     */
    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header)) {
            if (header.toLowerCase().startsWith("bearer ")) {
                return header.substring(7).trim();
            }
            return header.trim();
        }
        String token = request.getHeader("token");
        return StringUtils.hasText(token) ? token.trim() : null;
    }

    /**
     * 直接输出 JSON（HTTP 状态码保持 200，业务 code 为 401/403，方便前端统一按 code 判断）
     */
    private boolean reject(HttpServletResponse response, int code, String msg) throws IOException {
        UserContext.clear();
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(code, msg)));
        return false;
    }
}
