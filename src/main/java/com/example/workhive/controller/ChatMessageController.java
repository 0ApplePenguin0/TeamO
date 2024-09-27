package com.example.workhive.controller;

import com.example.workhive.domain.dto.ChatMessageDTO;
import com.example.workhive.repository.ChatRoomRepository;
import com.example.workhive.service.ChatMessageService;
import com.example.workhive.service.ChatRoomService;

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
        List<ChatMessageDTO> messages = chatMessageService.getMessagesByChatRoom(chatRoomId);
        return ResponseEntity.ok(messages);
    }

    // 메시지 전송
    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestBody ChatMessageDTO messageDTO) {
        chatMessageService.saveMessage(messageDTO);
        return ResponseEntity.ok().build();
    }
}
