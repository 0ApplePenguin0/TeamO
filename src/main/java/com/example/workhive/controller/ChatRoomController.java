package com.example.workhive.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.workhive.domain.dto.ChatRoomDTO;
import com.example.workhive.domain.dto.ProjectMemberDTO;
import com.example.workhive.domain.entity.CompanyEntity;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.repository.CompanyRepository;
import com.example.workhive.repository.MemberRepository;
import com.example.workhive.service.ChatRoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    //현재 로그인한 사용자의 채팅방 목록 불러오기
    @GetMapping("/getChatRoomsByUser/{userId}")
    public ResponseEntity<List<String>> getChatRoomsByUser(@PathVariable("userId") String userId) {
        List<String> chatRoomNames = chatRoomService.getChatRoomsByUser(userId);
        if (chatRoomNames.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        else
        {
        	return ResponseEntity.ok(chatRoomNames);	
        }
    }
   
   
    @PostMapping("/add")
    public ResponseEntity<String> createRoom(@RequestBody ChatRoomDTO chatRoomDTO) {
        log.debug("ChatRoomController 통과");

        // 서비스에서 채팅방 생성 로직 호출
        chatRoomService.createChatRoom(chatRoomDTO);

        return ResponseEntity.ok("채팅방이 생성되었습니다.");
    }

 // 사용자를 채팅방에 초대
    @PostMapping("/invite")
    public ResponseEntity<String> inviteUserToChatRoom(@RequestBody ChatRoomDTO chatRoomDTO) {
        log.debug("사용자 초대 기능 호출 - chatRoomId: {}, memberId: {}", chatRoomDTO.getChatRoomId(), chatRoomDTO.getCreatedByMemberId());

        Long chatRoomId = chatRoomDTO.getChatRoomId();
        String memberId = chatRoomDTO.getCreatedByMemberId(); // 초대할 사용자 ID는 CreatedByMemberId로 받는다고 가정
        
        boolean success = chatRoomService.inviteUserToChatRoom(chatRoomId, memberId);
        
        if (success) {
            return ResponseEntity.ok("사용자가 채팅방에 초대되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("초대에 실패했습니다.");
        }
    }

}