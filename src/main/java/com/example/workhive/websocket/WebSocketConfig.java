package com.example.workhive.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket  // WebSocket을 활성화하는 설정
@RequiredArgsConstructor  // 롬복을 사용하여 생성자 자동 생성
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatHandler handler;  // WebSocket 핸들러
    private final ChatHandshakeInterceptor chatHandshakeInterceptor;  // 우리가 생성한 인터셉터

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/chat")
                .addInterceptors(new HttpSessionHandshakeInterceptor(), chatHandshakeInterceptor)  // 세션 정보와 chatRoomId 인터셉터 추가
                .setAllowedOrigins("http://localhost:8888"); // 특정 출처만 허용 (필요에 따라 수정)
    }
}
