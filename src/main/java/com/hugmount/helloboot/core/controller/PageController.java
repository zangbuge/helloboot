package com.hugmount.helloboot.core.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/** 处理所有页面跳转
 * @Author Li Huiming
 * @Date 2019/9/6
 */

@Controller
@RequestMapping("/page")
@Slf4j
public class PageController {

    @RequestMapping("/{level1}")
    public String level1(@PathVariable("level1") String level1) {

        String htmlUrl = dealHtmlUrl(level1);
        return htmlUrl;
    }

    @RequestMapping("/{level1}/{level2}")
    public String level2(@PathVariable("level1") String level1
        ,@PathVariable("level2") String level2) {

        String htmlUrl = dealHtmlUrl(level1, level2);
        return htmlUrl;
    }

    @RequestMapping("/{level1}/{level2}/{level3}")
    public String level3(@PathVariable("level1") String level1
            ,@PathVariable("level2") String level2
            ,@PathVariable("level3") String level3) {

        String htmlUrl = dealHtmlUrl(level1, level2, level3);
        return htmlUrl;
    }

    @RequestMapping("/{level1}/{level2}/{level3}/{level4}")
    public String level4(@PathVariable("level1") String level1
            ,@PathVariable("level2") String level2
            ,@PathVariable("level3") String level3
            ,@PathVariable("level4") String level4) {

        String htmlUrl = dealHtmlUrl(level1, level2, level3, level4);
        return htmlUrl;
    }


    public String dealHtmlUrl(String... level) {
        StringBuilder htmlPath = new StringBuilder();
        for (String lv : level) {
            htmlPath.append(lv).append("/");
        }
        String str = htmlPath.toString();
        str = str.substring(0, str.length() - 1);
        log.info("跳转的页面: " + str);
        return str;
    }


}
