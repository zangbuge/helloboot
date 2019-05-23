package com.hugmount.helloboot.config;

import com.hugmount.helloboot.websocket.WebsocketInterceptHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.HashSet;
import java.util.Set;

/** spring低层实现websocket
 * @Author: Li Huiming
 * @Date: 2019/5/17
 */
@Configuration
@EnableWebSocket
@Slf4j
public class WebSocketConfig implements WebSocketConfigurer{


    public final static String WEBSOCKET_USERNAME = "WEBSOCKET_USERNAME";

    /**
     * 映射请求
     * @param webSocketHandlerRegistry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        // 将 MarcoHandler 映射到 "/websocket/marco"
        String mapping = "/websocket/marco";
        webSocketHandlerRegistry.addHandler(marcoHandler() ,mapping)
                                //添加拦截器
                                .addInterceptors(new WebsocketInterceptHandler());
    }

    @Bean
    public MarcoHandler marcoHandler () {
        return new MarcoHandler();
    }

    public static class MarcoHandler extends AbstractWebSocketHandler{

        protected static Set<WebSocketSession> userSessionSet = new HashSet<>();


        @Override
        protected void handleTextMessage (WebSocketSession socketSession , TextMessage textMessage){
            try {
                log.info("hello webSocket send success");
//                Thread.sleep(1000); //延迟1秒
                //发送socket信息
                socketSession.sendMessage(textMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 连接成功通知
         * @param session
         * @throws Exception
         */
        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            userSessionSet.add(session);
            log.info("用户上线, 在线用户数量: " + userSessionSet.size());
            //也可在此实现自己的业务
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            userSessionSet.remove(session);
            log.info("用户下线, 在线用户数量: " + userSessionSet.size());
        }


        public void sendMsgToUser (Set<String> userNameSet, TextMessage textMessage) {
            for (WebSocketSession socketSession : userSessionSet) {
                Object targetUser = socketSession.getAttributes().get(WEBSOCKET_USERNAME);
                try {
                    if (socketSession.isOpen()) {
                        for (String user : userNameSet) {
                            if (targetUser.equals(user)) {
                                socketSession.sendMessage(textMessage);
                                log.info("websocket推送给用户成功,目标用户: " + targetUser);
                            }
                            else {
                                log.info("websocket推送失败, 核对目标用户: " + user + " ,session中目标用户: " + targetUser);
                            }
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("websocket推送给用户失败, 系统异常");
                    return;
                }

            }

        }

    }

}
