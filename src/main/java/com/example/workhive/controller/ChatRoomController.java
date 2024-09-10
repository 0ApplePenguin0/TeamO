package com.example.workhive.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.workhive.domain.dto.ChatRoomDTO;
import com.example.workhive.domain.dto.MemberDTO;
import com.example.workhive.service.ChatRoomService;
import com.example.workhive.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final MemberService memberservice;

    // 채팅방 목록 불러오기
    @GetMapping
    public List<ChatRoomDTO> getAllRooms() {
        return chatRoomService.getAllChatRooms();
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


 // 회원 리스트 반환 API
    @GetMapping("/members")
    public List<MemberDTO> getAllMembers() {
        return memberservice.getAllMembers();  // memberService에서 모든 회원을 반환하는 메서드 호출
    }
}