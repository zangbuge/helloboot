package com.hugmount.helloboot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

/** spring低层实现websocket
 * @Author: Li Huiming
 * @Date: 2019/5/17
 */
@Configuration
@EnableWebSocket
@Slf4j
public class WebSocketConfig implements WebSocketConfigurer{


    /**
     * 映射请求
     * @param webSocketHandlerRegistry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        // 将 MarcoHandler 映射到 "/websocket/marco"
        String mapping = "/websocket/marco";
        webSocketHandlerRegistry.addHandler(marcoHandler() ,mapping);
    }

    @Bean
    public MarcoHandler marcoHandler () {
        return new MarcoHandler();
    }

    class MarcoHandler extends AbstractWebSocketHandler{

        @Override
        protected void handleTextMessage (WebSocketSession socketSession , TextMessage textMessage){
            try {
                log.info("hello webSocket how are you ok");
//                Thread.sleep(1000); //延迟1秒
                //发送socket信息
                socketSession.sendMessage(textMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
