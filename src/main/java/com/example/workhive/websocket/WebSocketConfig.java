package com.example.workhive.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor  // 롬복을 사용하여 생성자 자동 생성
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatHandler handler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/chat/{roomId}")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .setAllowedOrigins("*"); // 모든 출처 허용

        // SockJS를 사용하는 경우와 아닌 경우를 명확히 구분
        // SockJS를 사용하지 않으려면 아래 주석 처리
        //.withSockJS(); // 필요 시 SockJS 활성화

        // 만약 SockJS를 사용하지 않으려면 아래 코드를 사용
        // registry.addHandler(handler, "/ws/chat/{roomId}").setAllowedOrigins("*"); // SockJS 없이 WebSocket 설정
    }
}
