package com.example.workhive.controller;

import com.example.workhive.domain.dto.ChatMessageDTO;
import com.example.workhive.service.ChatMessageService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    // 이미지 업로드 메서드 추가
    // 이미지 업로드 메서드
    @PostMapping("/uploadImage")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile image) {
        try {
            // 이미지 파일이 비어 있는지 확인
            if (image.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미지가 비어 있습니다.");
            }

            // 이미지 저장 및 URL 반환
            String imageUrl = chatMessageService.saveImage(image);
            return ResponseEntity.status(HttpStatus.CREATED).body(imageUrl); // 성공적으로 저장되면 이미지 URL 반환
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 업로드 실패: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 업로드 중 오류 발생: " + e.getMessage());
        }
    }

}
