package com.example.workhive.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatHandler handler;

    public WebSocketConfig(ChatHandler handler) {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // WebSocket 엔드포인트를 "/ws/chat/{roomId}"로 설정하여 각 채팅방 별로 연결 가능
        registry.addHandler(handler, "/ws/chat/{roomId}")
                .addInterceptors(new HttpSessionHandshakeInterceptor()) // 세션을 사용한 핸드쉐이크 인터셉터 추가
                .setAllowedOrigins("*");  // CORS 설정: 모든 오리진 허용
    }
}
