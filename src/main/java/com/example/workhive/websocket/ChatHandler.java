package com.example.workhive.websocket;

import com.example.workhive.domain.dto.ChatMessageDTO;
import com.example.workhive.service.ChatMessageService;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class ChatHandler extends TextWebSocketHandler {

    // 고정된 채팅방 (전체 채팅방) ID
    private static final String CHAT_ROOM_ID = "24";
    
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();  // 연결된 모든 세션을 관리
    private final ChatMessageService chatMessageService;  // 메시지 저장을 위한 서비스
    private final ObjectMapper objectMapper = new ObjectMapper();  // JSON 파싱을 위한 객체

    public ChatHandler(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    // WebSocket 연결이 성공했을 때 실행
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        System.out.println("새로운 연결: " + session.getId());
    }

    // 수신한 메시지를 처리하는 로직
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            String payload = message.getPayload();
            ChatMessageDTO chatMessageDTO = objectMapper.readValue(payload, ChatMessageDTO.class);

            // 메시지를 브로드캐스트하고, DB에 저장
            chatMessageDTO.setChatRoomId(Long.valueOf(CHAT_ROOM_ID)); // 고정된 채팅방 ID 사용
            chatMessageService.saveMessage(chatMessageDTO);  // 메시지 저장

            // 해당 메시지를 모든 연결된 세션으로 브로드캐스트
            broadcastMessageToAllSessions("[" + chatMessageDTO.getMemberId() + "]: " + chatMessageDTO.getMessage());

        } catch (Exception e) {
            System.err.println("메시지 처리 중 오류 발생: " + e.getMessage());
            session.close(CloseStatus.SERVER_ERROR);  // 처리 중 오류 발생 시 연결 종료
        }
    }

    // WebSocket 연결이 종료되었을 때 실행
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        System.out.println("연결이 종료됨: " + session.getId());
    }

    // 모든 세션에 메시지를 브로드캐스트하는 로직
    private void broadcastMessageToAllSessions(String message) throws Exception {
        for (WebSocketSession session : sessions.values()) {
            if (session.isOpen()) {  // 세션이 열려있는 경우에만 메시지 전송
                session.sendMessage(new TextMessage(message));
            }
        }
    }
}
