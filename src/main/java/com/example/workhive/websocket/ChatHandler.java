package com.example.workhive.websocket;

import com.example.workhive.domain.dto.ChatMessageDTO;
import com.example.workhive.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper; // ObjectMapper 임포트 추가

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ChatHandler extends TextWebSocketHandler {

    // 채팅방 별로 세션을 관리하는 Map
    private final Map<Long, Map<String, WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    private final ChatMessageService chatMessageService;  // 메시지 저장을 위한 서비스
    private final ObjectMapper objectMapper = new ObjectMapper();  // ObjectMapper 초기화

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // WebSocketSession에 chatRoomId를 저장
        Long chatRoomId = (Long) session.getAttributes().get("chatRoomId");

        // 전체 채팅방(25) 및 팀 채팅방(24) 처리
        if (chatRoomId == null) {
            chatRoomId = 25L; // 기본값으로 전체 채팅방으로 설정
        }

        roomSessions.computeIfAbsent(chatRoomId, k -> new ConcurrentHashMap<>()).put(session.getId(), session);
    }

    // 수신한 메시지를 처리하는 로직
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        ChatMessageDTO chatMessageDTO = objectMapper.readValue(payload, ChatMessageDTO.class);

        // 메시지를 저장하고, 해당 채팅방에 브로드캐스트
        chatMessageService.saveMessage(chatMessageDTO);  // 메시지 저장
        broadcastMessageToRoom(chatMessageDTO.getChatRoomId(), 
                               "[" + chatMessageDTO.getMemberId() + "]: " + chatMessageDTO.getMessage());
    }

    // 특정 채팅방에 메시지를 브로드캐스트하는 로직
    private void broadcastMessageToRoom(Long chatRoomId, String message) throws Exception {
        if (roomSessions.containsKey(chatRoomId)) {
            for (WebSocketSession session : roomSessions.get(chatRoomId).values()) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            }
        }
    }

    // WebSocket 연결이 종료되었을 때 실행
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        roomSessions.forEach((key, value) -> value.remove(session.getId()));
    }
}
