package com.example.workhive.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.workhive.domain.dto.ChatRoomDTO;
import com.example.workhive.service.ChatRoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 현재 로그인한 사용자의 채팅방 목록 불러오기
    @GetMapping("/getChatRoomsByUser/{userId}")
    public List<ChatRoomDTO> getChatRoomsByUser(@PathVariable("userId") String userId) {
        return chatRoomService.getChatRoomsByUserId(userId);
    }

    @PostMapping("/invite")
    public ResponseEntity<String> inviteUserToChatRoom(@RequestBody Map<String, String> request) {
        String memberId = request.get("memberId");
        String roomName = request.get("roomName");
        log.debug("멤버 아이디 = ", memberId, "방 이름", roomName);
        chatRoomService.inviteUserToChatRoom(memberId, roomName);
        return ResponseEntity.ok("사용자가 초대되었습니다.");
    }

    
    // 채팅방 추가하기
    @PostMapping("/add")
    public ResponseEntity<String> createRoom(@RequestBody ChatRoomDTO chatRoomDTO) {
        chatRoomService.createChatRoom(chatRoomDTO);
        return ResponseEntity.ok("채팅방이 생성되었습니다.");
    }

    // 채팅방 삭제하기
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteRoom(@PathVariable("id") Integer id) {
        chatRoomService.deleteChatRoom(id);
        log.debug("test, deleted num: {} ", id);
        return ResponseEntity.ok("채팅방이 삭제되었습니다.");
    }


}