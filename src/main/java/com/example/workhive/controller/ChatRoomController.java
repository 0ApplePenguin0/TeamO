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
import com.example.workhive.domain.entity.ChatRoomEntity;
import com.example.workhive.repository.ChatRoomRepository;
import com.example.workhive.service.ChatRoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatRoomRepository chatRoomRepository;
    
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

    @PostMapping("/invite")
    public ResponseEntity<String> inviteUserToChatRoom(@RequestBody ChatRoomDTO chatRoomDTO) {
        log.debug("사용자 초대 기능 호출 - chatRoomName: {}, memberId: {}", chatRoomDTO.getChatRoomName(), chatRoomDTO.getCreatedByMemberId());

        // chatRoomName과 memberId로 채팅방 조회
        String chatRoomName = chatRoomDTO.getChatRoomName();
        String memberId = chatRoomDTO.getCreatedByMemberId();

        // 채팅방 이름과 생성자 ID로 ChatRoomEntity 조회 (여기서 chatRoomRepository를 사용)
        ChatRoomEntity chatRoom = chatRoomRepository.findByChatRoomNameAndCreatedByMember_MemberId(chatRoomName, memberId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        // 조회된 chatRoom의 ID를 사용하여 inviteUserToChatRoom 호출
        log.debug("chatRoom 테스트: ", chatRoom);
        Long chatRoomId = chatRoom.getChatRoomId();

        boolean success = chatRoomService.inviteUserToChatRoom(chatRoomId, memberId);

        if (success) {
            return ResponseEntity.ok("사용자가 채팅방에 초대되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("초대에 실패했습니다.");
        }
    }

}