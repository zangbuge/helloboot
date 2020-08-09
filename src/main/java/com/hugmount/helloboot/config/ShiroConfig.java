package com.hugmount.helloboot.config;

import com.hugmount.helloboot.test.pojo.User;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.apache.shiro.mgt.SecurityManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Li Huiming
 * @Date 2019/8/17
 */

@Configuration
public class ShiroConfig {

    @Bean
    public Realm realm() {
        //创建自己的Realm实例
        return new UserRealm();
    }

    @Bean
    public DefaultWebSessionManager getDefaultWebSessionManager() {
        DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
        defaultWebSessionManager.setGlobalSessionTimeout(1000 * 60 * 30);// 会话过期时间,单位：毫秒(在无操作时开始计时)
        defaultWebSessionManager.setSessionValidationSchedulerEnabled(true);
        defaultWebSessionManager.setSessionIdCookieEnabled(true);
        // shiro默认设置会重写url, 给加上jsessionid. 设为false, 则不会在url上拼jsessionid
        defaultWebSessionManager.setSessionIdUrlRewritingEnabled(false);
        return defaultWebSessionManager;
    }

    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager() {
        DefaultWebSecurityManager dwsm = new DefaultWebSecurityManager();
        dwsm.setRealm(realm());// 自定义realm
        dwsm.setSessionManager(getDefaultWebSessionManager());// session管理
        return dwsm;
    }


    // 设置对应的过滤条件和跳转条件
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        Map<String, String> map = new HashMap<>();
        // 表示可以匿名访问
        map.put("/index", "anon");
        map.put("/login", "anon");
        map.put("/js", "anon");
        map.put("/css", "anon");
        map.put("/logout", "logout");
        // authc :所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问
        map.put("/**", "anon");

        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        shiroFilterFactoryBean.setLoginUrl("/index");
        // 登录成功以后
        shiroFilterFactoryBean.setSuccessUrl("/index");
        // 错误页面，认证不通过跳转
        shiroFilterFactoryBean.setUnauthorizedUrl("/error");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        return shiroFilterFactoryBean;
    }



    /**
     * 设置对应的过滤条件和跳转条件
     * @return
     */
    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chain = new DefaultShiroFilterChainDefinition();

//        // 访问控制 ,注意过滤器配置顺序 不能颠倒
//        chain.addPathDefinition("/index", "anon");// 可以匿名访问
//        chain.addPathDefinition("/404", "anon");//404页面
//        chain.addPathDefinition("/login", "anon");//登录不能拦截
//        chain.addPathDefinition("/css/**", "anon");//静态资源文件
//        chain.addPathDefinition("/js/**", "anon");
//        // 其它路径均需要登录
//        chain.addPathDefinition("/**", "authc");//其他使用注解判断

        //不需要在此处配置权限页面,因为上面的ShiroFilterFactoryBean已经配置过,但是此处必须存在,
        return chain;
    }


    /**
     * 验证以及权限添加（自定义权限添加及检查）
     */
    static class UserRealm extends AuthorizingRealm {

        /**
         *  添加权限
         * @param principalCollection
         * @return
         */
        @Override
        protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
            //获取登录用户名
            String name= (String) principalCollection.getPrimaryPrincipal();
            //查询用户名称（这里可以使用缓存来做），这里根据项目情况不同，选择不同的操作
//            User user = loginService.findByName(name);
            String user = "hugmount";

            //添加角色和权限
            SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
//            for (Role role:user.getRoles()) {
//                //添加角色
//                simpleAuthorizationInfo.addRole(role.getRoleName());
//                for (Permission permission:role.getPermissions()) {
//                    //添加权限
//                    simpleAuthorizationInfo.addStringPermission(permission.getPermission());
//                }
//            }

            simpleAuthorizationInfo.addStringPermission("all");
            return simpleAuthorizationInfo;
        }


        /**
         * 验证权限, 调用currUser.login(token)方法时会调用doGetAuthenticationInfo方法
         * @param authenticationToken
         * @return
         * @throws AuthenticationException
         */
        @Override
        protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
            //加这一步的目的是在Post请求的时候会先进认证，然后在到请求
            if (authenticationToken.getPrincipal() == null) {
                return null;
            }
            //获取shiro中用户信息
            String name = authenticationToken.getPrincipal().toString();
            System.out.println("shiro用户信息: " + name);
            // 从数据库获取用户信息
            User user = new User();
            user.setUsername("hugmount");
            user.setPassword("password");
            if (user == null) {
                //这里返回后会报出对应异常
                return null;
            }
            else {
                //这里验证authenticationToken和simpleAuthenticationInfo的信息, 匹配成功才验证通过
                SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(user.getUsername(), user.getPassword(), getName());
                return simpleAuthenticationInfo;
            }
        }

    }

/**
 用户登录成功后，通过
 SecurityUtils.getSubject().getSession().setAttribute("user",userTo);

 设置shiro的session，开启一个线程加载列表，在列表中获取session
 SecurityUtils.getSubject().getSession().getAttribute("user");
 */


}
