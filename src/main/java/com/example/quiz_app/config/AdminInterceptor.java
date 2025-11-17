package com.example.quiz_app.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect("/");
            return false;
        }

        Object roleObj = session.getAttribute("role");
        if (roleObj == null) {
            response.sendRedirect("/");
            return false;
        }

        String role = roleObj.toString();
        if (!"admin".equalsIgnoreCase(role)) {
            response.sendRedirect("/");
            return false;
        }

        return true;
    }
}
