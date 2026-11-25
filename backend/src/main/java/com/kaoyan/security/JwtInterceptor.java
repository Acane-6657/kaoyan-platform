package com.kaoyan.security;

import com.kaoyan.common.util.JwtUtil;
import com.kaoyan.config.KaoyanProperties;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final KaoyanProperties properties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = resolveToken(request);
        Claims claims = jwtUtil.parse(token);
        LoginUser user = new LoginUser(
                jwtUtil.getUserId(claims),
                jwtUtil.getUsername(claims),
                jwtUtil.getRoles(claims)
        );
        UserContext.set(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(properties.getJwt().getHeader());
        String prefix = properties.getJwt().getPrefix();
        if (StringUtils.hasText(header)) {
            return header.startsWith(prefix) ? header.substring(prefix.length()).trim() : header.trim();
        }
        // 统一从请求头读取令牌；SSE 由前端使用 fetch 携带 Authorization，避免令牌进入 URL 和日志
        return "";
    }
}
