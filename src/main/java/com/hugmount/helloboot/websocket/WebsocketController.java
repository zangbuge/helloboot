package com.hugmount.helloboot.websocket;

import com.hugmount.helloboot.config.WebSocketConfig;
import com.hugmount.helloboot.core.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.TextMessage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashSet;

/**
 * @Author: Li Huiming
 * @Date: 2019/5/22
 */

@Slf4j
@Controller
@ResponseBody
@RequestMapping("/websocket")
public class WebsocketController {

    @Autowired
    WebSocketConfig webSocketConfig;

    @GetMapping("/login")
    public Result<String> login (String userName , HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute(WebSocketConfig.WEBSOCKET_USERNAME, userName);
        log.info("websocket登录成功");
        return Result.createBySuccess("success");
    }

    @PostMapping("/send")
    public Result<String> send (String targetUser ,String msg) {
        WebSocketConfig.MarcoHandler marcoHandler = webSocketConfig.marcoHandler();
        HashSet<String> uers = new HashSet<>();
        uers.add(targetUser);
        TextMessage textMessage = new TextMessage(msg);
        marcoHandler.sendMsgToUser(uers ,textMessage);
        log.info("websoket发送信息完成");
        return Result.createBySuccess("success");
    }

}
