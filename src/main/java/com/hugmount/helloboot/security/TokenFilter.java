package com.hugmount.helloboot.security;

import lombok.SneakyThrows;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: lhm
 * @date: 2023/8/24
 */

@Component
public class TokenFilter extends OncePerRequestFilter {

    private static final String TOKEN = "token";

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        String token = request.getHeader(TOKEN);
        if (StringUtils.isEmpty(token)) {
            filterChain.doFilter(request, response);
            return;
        }
        User obj = (User) redisTemplate.opsForValue().get(token);
        Assert.notNull(obj, "登录已失效");
        // 权限信息添加到上下文中
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(obj,
                null, obj.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

}
