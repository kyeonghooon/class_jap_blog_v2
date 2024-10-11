package com.tenco.blog_v1.common.config;

import com.tenco.blog_v1.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false); // 기존 세션이 없다면 null 반환 한다.
        User sessionUser =  (User) session.getAttribute("sessionUser");
        if (!sessionUser.getRole().equals("admin")) {
            return false;
        }
        return true;
    }
}
