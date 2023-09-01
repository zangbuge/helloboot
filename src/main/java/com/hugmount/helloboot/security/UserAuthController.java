package com.hugmount.helloboot.security;

import cn.hutool.core.lang.UUID;
import com.hugmount.helloboot.core.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author: lhm
 * @date: 2023/8/24
 */
@RestController
public class UserAuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/userLogin")
    public Result<String> login(String username, String password) {
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username, password);
        try {
            // 该方法会去调用 UserDetailsServiceImpl.loadUserByUsername
            Authentication authenticate = authenticationManager.authenticate(upToken);
            String token = UUID.randomUUID().toString().replace("-", "");
            User user = (User) authenticate.getPrincipal();
            redisTemplate.opsForValue().set(token, user, 30, TimeUnit.MINUTES);
            return Result.createBySuccess("登录成功", token);
        } catch (Exception e) {
            return Result.createByError("认证失败");
        }
    }

}
