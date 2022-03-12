package com.hugmount.helloboot.websocket;

import com.hugmount.helloboot.config.WebSocketConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;

/** websocket拦截器
 * @Author: Li Huiming
 * @Date: 2019/5/17
 */

@Slf4j
@Configuration
public class WebsocketInterceptHandler extends HttpSessionHandshakeInterceptor{


    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        log.debug("websocket before handshake");
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverHttpRequest = (ServletServerHttpRequest) request;
            // false 表示没有则返回 null
            HttpSession httpSession = serverHttpRequest.getServletRequest().getSession();
            if (null != httpSession) {
                String userName = (String) httpSession.getAttribute(WebSocketConfig.WEBSOCKET_USERNAME);
                if (null != userName){
                    attributes.put(WebSocketConfig.WEBSOCKET_USERNAME ,userName);
                    log.info("websocketSession中添加用户成功");
                }
                else {
                    log.info("websocketSession中添加用户失败");
                }
            }
        }

        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
}
