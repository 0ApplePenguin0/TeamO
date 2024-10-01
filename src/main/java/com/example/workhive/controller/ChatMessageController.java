package com.example.workhive.controller;

import com.example.workhive.domain.dto.ChatMessageDTO;
import com.example.workhive.service.ChatMessageService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat/messages")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    // 특정 채팅방의 메시지 조회
    @GetMapping("/{chatRoomId}")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(@PathVariable("chatRoomId") Long chatRoomId) {
        // 여전히 채팅 내역을 불러오는 API는 유지
        List<ChatMessageDTO> messages = chatMessageService.getMessagesByChatRoom(chatRoomId);
        return ResponseEntity.ok(messages);
    }

    // 메시지 전송 부분 제거 (웹소켓으로 대체되므로 HTTP로 메시지 전송 필요 없음)
    // 이 부분은 필요 없다면 완전히 제거해도 됩니다.
}
