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

    // 채팅방 별로 연결된 세션을 저장할 수 있는 맵 (roomId -> 세션맵)
    private Map<String, Map<String, WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    private final ChatMessageService chatMessageService;  // 메시지 저장을 위한 서비스
    private final ObjectMapper objectMapper = new ObjectMapper();  // JSON 파싱을 위한 객체

    public ChatHandler(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    // 특정 채팅방에 세션을 추가
    public void addSessionToRoom(String roomId, WebSocketSession session) {
        roomSessions.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).put(session.getId(), session);
    }

    // 특정 채팅방에서 세션을 제거
    public void removeSessionFromRoom(String roomId, WebSocketSession session) {
        if (roomSessions.containsKey(roomId)) {
            roomSessions.get(roomId).remove(session.getId());
            if (roomSessions.get(roomId).isEmpty()) {
                roomSessions.remove(roomId);
            }
        }
    }

    // 클라이언트가 연결되었을 때 실행
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomId = getRoomIdFromUri(session);
        addSessionToRoom(roomId, session);
        System.out.println("새로운 연결: " + session.getId() + " 채팅방: " + roomId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 수신한 메시지를 JSON 형태로 파싱
        String payload = message.getPayload();
        ObjectMapper objectMapper = new ObjectMapper();
        ChatMessageDTO chatMessageDTO = objectMapper.readValue(payload, ChatMessageDTO.class);

        // 파싱된 데이터를 이용해 메시지를 브로드캐스트하고 DB에 저장
        chatMessageService.saveMessage(chatMessageDTO); // 메시지 저장
        broadcastMessageToRoom(chatMessageDTO.getChatRoomId().toString(), "[" + chatMessageDTO.getMemberId() + "]: " + chatMessageDTO.getMessage());
    }

    // 연결이 종료되었을 때 실행
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = getRoomIdFromUri(session);
        removeSessionFromRoom(roomId, session);
        System.out.println("연결이 종료됨: " + session.getId() + " 채팅방: " + roomId);
    }

    // 특정 채팅방에 메시지 브로드캐스트
    public void broadcastMessageToRoom(String roomId, String message) throws Exception {
        if (roomSessions.containsKey(roomId)) {
            for (WebSocketSession session : roomSessions.get(roomId).values()) {
                session.sendMessage(new TextMessage(message));
            }
        }
    }

    // URI에서 roomId를 추출하는 유틸리티 메소드
    private String getRoomIdFromUri(WebSocketSession session) {
        String uri = session.getUri().toString();
        return uri.substring(uri.lastIndexOf('/') + 1);  // URI에서 roomId 추출
    }
}
