package com.example.workhive.websocket;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class chatHandler extends TextWebSocketHandler {

    // 채팅방 별로 연결된 세션을 저장할 수 있는 맵
    private Map<String, Map<String, WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

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
        // 임의로 roomId를 지정 (이 부분은 로직에 맞게 수정 필요)
        String roomId = "defaultRoom";  // 실제로는 클라이언트로부터 채팅방 정보를 받아야 합니다.
        addSessionToRoom(roomId, session);
        System.out.println("새로운 연결: " + session.getId() + " 채팅방: " + roomId);
    }

    // 메시지를 수신했을 때 실행
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            System.out.println("수신된 메시지: " + message.getPayload());
            String roomId = "defaultRoom";  // 실제로는 메시지와 함께 채팅방 정보를 받아야 합니다.
            broadcastMessageToRoom(roomId, message.getPayload());
        } catch (Exception e) {
            System.err.println("메시지 전송 중 오류 발생: " + e.getMessage());
        }
    }

    // 연결이 종료되었을 때 실행
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = "defaultRoom";  // 실제로는 클라이언트로부터 채팅방 정보를 받아야 합니다.
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
}
