package com.hugmount.helloboot.test.controller;

import com.alibaba.fastjson.JSON;
import com.hugmount.helloboot.test.pojo.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @Author Li Huiming
 * @Date 2019/8/17
 */

@Controller
public class UserController {


    @ResponseBody
    @PostMapping("/login")
    public String login(@RequestBody User user) {
        System.out.println(JSON.toJSONString(user));
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
        Subject currentUser = SecurityUtils.getSubject();
        try {
            // 登录
            currentUser.login(token);
            return JSON.toJSONString(user);

        } catch (UnknownAccountException uae) {
            return "账号不正确!";

        } catch (IncorrectCredentialsException ice) {
            return "密码不正确!";

        } catch (LockedAccountException lae) {
            return "账号被锁定!";

        } catch (AuthenticationException ae) {
            return "登录出错!";
        }

    }


    @ResponseBody
    @GetMapping("/logout")
    public String logOut() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        System.out.println("已退出");
        return "logout--->已退出";
    }


//    @RequiresRoles("xxxx") 这里配置的是哪些角色可以使用
//    @RequiresPermissions("xxxxxxx")这里配置的是哪些权限可以使用
    // spring-security 使用见authdemo项目
    @ResponseBody
    @GetMapping("/testHello")
    public String testHello() {
        System.out.println("测试权限");

        return "testHello";
    }


    /**
     *  主页
     * @return
     */
    @GetMapping("/index")
    public String index() {
        return "index";
    }

}
