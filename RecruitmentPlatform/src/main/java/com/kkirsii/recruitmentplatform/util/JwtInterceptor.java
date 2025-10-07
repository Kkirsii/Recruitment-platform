package com.kkirsii.recruitmentplatform.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");

        if("OPTIONS".equals(request.getMethod().toUpperCase())) {
            System.out.println("Method:OPTIONS");
            return true;
        }
        if (token == null || !JwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\": \"Unauthorized: Invalid or missing token\"}");
            return false;
        }
        String role = JwtUtil.getRole(token);
        String requestURI = request.getRequestURI();
        String []path = requestURI.split("/");
        if(!path[1].equals(role))
        {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\": \"Unauthorized: Invalid or missing token\"}");
            return false;
        }
        String email = JwtUtil.getEmail(token);
        UserContext.setEmail(email);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear(); // 避免内存泄露
    }
}
