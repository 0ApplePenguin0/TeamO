package com.example.workhive.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    
    @GetMapping("/checkChatRoomName/{chatRoomName}")
    public ResponseEntity<Boolean> checkChatRoomName(@PathVariable("chatRoomName") String chatRoomName) {
        boolean exists = chatRoomRepository.existsByChatRoomName(chatRoomName);
        return ResponseEntity.ok(exists); 
    }
    
    @GetMapping("/getChatRoomKind/{chatRoomId}")
    public ResponseEntity<Long> getChatRoomKind(@PathVariable("chatRoomId") Long chatRoomId) {
        Optional<ChatRoomEntity> chatRoom = chatRoomRepository.findById(chatRoomId);
        if (chatRoom.isPresent()) {
            return ResponseEntity.ok(chatRoom.get().getChatRoomKind().getChatroomKindId()); // chatRoomKindId를 반환
        }
        return ResponseEntity.notFound().build();
    }
    
    // 현재 로그인한 사용자의 채팅방 목록 불러오기
    @GetMapping("/getChatRoomsByUser/{userId}")
    public ResponseEntity<List<ChatRoomDTO>> getChatRoomsByUser(@PathVariable("userId") String userId) {
        List<ChatRoomDTO> chatRoomDTOs = chatRoomService.getChatRoomsByUser(userId);
        if (chatRoomDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(chatRoomDTOs);
        }
    }
   
    @GetMapping("/getChatRoomIdByName/{chatRoomName}")
    public ResponseEntity<Long> getChatRoomIdByName(@PathVariable("chatRoomName") String chatRoomName) {
        ChatRoomEntity chatRoom = chatRoomRepository.findByChatRoomName(chatRoomName)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
        return ResponseEntity.ok(chatRoom.getChatRoomId());
    }
    
    @PostMapping("/add")
    public ResponseEntity<String> createRoom(@RequestBody ChatRoomDTO chatRoomDTO) {
        log.debug("ChatRoomController 통과");

        // 서비스에서 채팅방 생성 로직 호출
        chatRoomService.createChatRoom(chatRoomDTO);

        return ResponseEntity.ok("채팅방이 생성되었습니다.");
    }
    @GetMapping("/participants/{chatRoomId}")
    public ResponseEntity<List<String>> getChatRoomParticipants(@PathVariable("chatRoomId") Long chatRoomId) {
        List<String> participants = chatRoomService.getChatRoomParticipants(chatRoomId);
        if (participants.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(participants);
    }

    @PostMapping("/invite")
    public ResponseEntity<String> inviteUserToChatRoom(@RequestBody ChatRoomDTO chatRoomDTO) {
        log.debug("사용자 초대 기능 호출 - chatRoomId: {}, memberId: {}", chatRoomDTO.getChatRoomId(), chatRoomDTO.getCreatedByMemberId());

        // chatRoomId와 memberId로 채팅방 조회
        Long chatRoomId = chatRoomDTO.getChatRoomId();
        String memberId = chatRoomDTO.getCreatedByMemberId();

        // 단일 채팅방 ID로 ChatRoomEntity 조회
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        // 초대 서비스 호출
        boolean success = chatRoomService.inviteUserToChatRoom(chatRoomId, memberId);

        if (success) {
            return ResponseEntity.ok(memberId + "님이 채팅방에 초대되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("초대에 실패했습니다.");
        }
    }


    @DeleteMapping("/delete/{chatRoomId}")
    public ResponseEntity<String> deleteChatRoom(@PathVariable("chatRoomId") Long chatRoomId) {
        log.debug("채팅방 삭제 요청 - chatRoomId: {}", chatRoomId);

        // chatRoomId로 채팅방 조회
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        // 채팅방 삭제
        chatRoomRepository.delete(chatRoom);
        log.debug("채팅방 {} 삭제 완료", chatRoomId);

        return ResponseEntity.ok(chatRoomId + " 채팅방이 삭제되었습니다.");
    }
}
