package com.hugmount.helloboot.config;

import com.hugmount.helloboot.security.TokenFilter;
import com.hugmount.helloboot.security.UnauthorizedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author: lhm
 * @date: 2023/8/24
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private TokenFilter tokenFilter;

    @Autowired
    @Qualifier("customUserDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                // 基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                // 过滤请求
                .authorizeRequests()
                // 对于获取token的rest api要允许匿名访问
                .antMatchers("/userLogin", "/api/**").anonymous()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated();
        http.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
        // 匿名用户访问无权限资源时的异常
        http.exceptionHandling().authenticationEntryPoint(new UnauthorizedHandler());
        // 禁用缓存
        http.headers().cacheControl();
    }

    /**
     * 用户认证
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

/*    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            *//**
     * 自定义加密方式
     * @param rawPassword
     * @return
     *//*
            @Override
            public String encode(CharSequence rawPassword) {
                String password = rawPassword + "";
                return DigestUtils.md5Hex(password);
            }

            *//**
     * 校验密码
     * @param rawPassword
     * @param encodedPassword
     * @return
     *//*
            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                String password = rawPassword + "";
                String pwd = DigestUtils.md5Hex(password);
                return pwd.equals(encodedPassword);
            }
        };
    }*/
}
