package com.hugmount.helloboot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Configuration
public class SecurityInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession();
//        log.info("进入拦截器...");
        // 跳转登录
//        String url = "/login";
//        response.sendRedirect(url);

        //转发页面请求
//        String requestServletPath = request.getServletPath();
//        log.info("requestServletPath: " + requestServletPath);
//        int indexOf = requestServletPath.indexOf(".html");
//        if (indexOf > 0) {
//            String newUrl = requestServletPath.substring(0, indexOf);
//            log.info("newUrl: " + newUrl);
//            request.getRequestDispatcher(newUrl).forward(request, response);
//            return false;
//        }

        return true;
    }
}
