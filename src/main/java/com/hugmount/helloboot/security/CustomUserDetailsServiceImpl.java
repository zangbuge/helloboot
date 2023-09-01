package com.hugmount.helloboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: lhm
 * @date: 2023/8/24
 */
@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 因为security自身的设计原因，角色权限前面需要添加ROLE前缀
     */
    private static final String ROLE_PREFIX = "ROLE_";

    /**
     * 因为security自身的设计原因，我们在用户分组和角色权限，增加ROLE前缀
     *
     * @param role 角色
     * @return SimpleGrantedAuthority
     */
    private SimpleGrantedAuthority genSimpleGrantedAuthority(String role) {
        if (!role.startsWith(ROLE_PREFIX)) {
            role = ROLE_PREFIX + role;
        }
        return new SimpleGrantedAuthority(role);
    }

    /**
     * @param userName
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        // 用户权限列表
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        // 当前用户权限, 数据库获取
        String role = "test";
        grantedAuthorities.add(genSimpleGrantedAuthority(role));
        // 返回用户权限信息
        String pwd = "123456";
        String encode = new BCryptPasswordEncoder().encode(pwd);
        return new User("lhm", encode, grantedAuthorities);
    }
}
