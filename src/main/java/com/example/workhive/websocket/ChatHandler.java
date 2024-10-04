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
import java.util.Optional;

@Component
public class ChatHandler extends TextWebSocketHandler {

    // 채팅방 별로 연결된 세션을 저장하는 맵 (roomId -> 세션맵)
    private final Map<String, Map<String, WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    private final ChatMessageService chatMessageService;  // 메시지 저장을 위한 서비스
    private final ObjectMapper objectMapper = new ObjectMapper();  // JSON 파싱을 위한 객체

    public ChatHandler(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    // 특정 채팅방에 세션을 추가
    private void addSessionToRoom(String roomId, WebSocketSession session) {
        roomSessions.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).put(session.getId(), session);
        System.out.println("세션이 추가됨: " + session.getId() + " 채팅방: " + roomId);
    }

    // 특정 채팅방에서 세션을 제거
    private void removeSessionFromRoom(String roomId, WebSocketSession session) {
        Optional.ofNullable(roomSessions.get(roomId))
                .ifPresent(sessions -> {
                    sessions.remove(session.getId());
                    if (sessions.isEmpty()) {
                        roomSessions.remove(roomId);
                    }
                });
        System.out.println("세션이 제거됨: " + session.getId() + " 채팅방: " + roomId);
    }

    // WebSocket 연결이 성공했을 때 실행
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomId = getRoomIdFromUri(session);
        if (roomId != null) {
            addSessionToRoom(roomId, session);
            System.out.println("새로운 연결: " + session.getId() + " 채팅방: " + roomId);
        } else {
            System.err.println("URI에서 roomId 추출 실패: " + session.getUri());
            session.close(CloseStatus.BAD_DATA);  // URI에서 roomId 추출 실패 시 연결 종료
        }
    }

    // 수신한 메시지를 처리하는 로직
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            String payload = message.getPayload();
            ChatMessageDTO chatMessageDTO = objectMapper.readValue(payload, ChatMessageDTO.class);

            // 메시지를 브로드캐스트하고, DB에 저장
            chatMessageService.saveMessage(chatMessageDTO); // 메시지 저장
            broadcastMessageToRoom(chatMessageDTO.getChatRoomId().toString(),
                    "[" + chatMessageDTO.getMemberId() + "]: " + chatMessageDTO.getMessage());

        } catch (Exception e) {
            System.err.println("메시지 처리 중 오류 발생: " + e.getMessage());
            session.close(CloseStatus.SERVER_ERROR);  // 처리 중 오류 발생 시 연결 종료
        }
    }

    // WebSocket 연결이 종료되었을 때 실행
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = getRoomIdFromUri(session);
        if (roomId != null) {
            removeSessionFromRoom(roomId, session);
            System.out.println("연결이 종료됨: " + session.getId() + " 채팅방: " + roomId);
        }
    }

    // 특정 채팅방에 메시지를 브로드캐스트하는 로직
    private void broadcastMessageToRoom(String roomId, String message) throws Exception {
        Map<String, WebSocketSession> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            for (WebSocketSession session : sessions.values()) {
                if (session.isOpen()) {  // 세션이 열려있는 경우에만 메시지 전송
                    session.sendMessage(new TextMessage(message));
                }
            }
        }
    }

    // URI에서 roomId를 추출하는 유틸리티 메소드
    private String getRoomIdFromUri(WebSocketSession session) {
        try {
            String uri = session.getUri().toString();
            return uri.substring(uri.lastIndexOf('/') + 1);  // URI에서 roomId 추출
        } catch (Exception e) {
            System.err.println("URI에서 roomId 추출 중 오류 발생: " + e.getMessage());
            return null;
        }
    }
}
