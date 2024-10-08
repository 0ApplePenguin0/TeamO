package com.example.workhive.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.workhive.domain.dto.ChatRoomDTO;
import com.example.workhive.domain.dto.ChatRoomInviteDTO;
import com.example.workhive.domain.dto.MemberDTO;
import com.example.workhive.domain.entity.ChatRoomEntity;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.domain.entity.ProjectMemberEntity;
import com.example.workhive.repository.ChatRoomRepository;
import com.example.workhive.repository.ProjectMemberRepository;
import com.example.workhive.service.ChatRoomService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatRoomRepository chatRoomRepository;
    private final ProjectMemberRepository projectMemberRepository;
    
    // 채팅방 이름이 이미 존재하는지 확인
    @GetMapping("/checkChatRoomName/{chatRoomName}")
    public ResponseEntity<Boolean> checkChatRoomName(@PathVariable("chatRoomName") String chatRoomName) {
        boolean exists = chatRoomRepository.existsByChatRoomName(chatRoomName);
        return ResponseEntity.ok(exists);
    }
 // 이미 초대된 사용자 목록을 반환하는 API
    @GetMapping("/invitedUsers/{chatRoomId}")
    public ResponseEntity<List<MemberDTO>> getInvitedUsers(@PathVariable("chatRoomId") Long chatRoomId) {
        log.debug("초대된 사용자 목록 조회 - chatRoomId: {}", chatRoomId);

        // chatRoomId를 통해 해당 채팅방에 속한 사용자를 조회
        List<ProjectMemberEntity> projectMembers = projectMemberRepository.findByChatRoom_ChatRoomId(chatRoomId);
        List<MemberDTO> invitedUsers = new ArrayList<>();

        // 초대된 사용자들의 목록을 DTO로 변환
        for (ProjectMemberEntity projectMember : projectMembers) {
            MemberEntity member = projectMember.getMember();  // ProjectMemberEntity에서 MemberEntity를 가져옴
            MemberDTO memberDTO = MemberDTO.builder()
                    .memberId(member.getMemberId())
                    .memberName(member.getMemberName())
                    .build();
            invitedUsers.add(memberDTO);
        }

        // 초대된 사용자 목록이 없을 때는 빈 리스트 반환
        if (invitedUsers.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());  // 빈 배열 반환
        }

        return ResponseEntity.ok(invitedUsers);
    }

    // 특정 채팅방의 종류를 반환
    @GetMapping("/getChatRoomKind/{chatRoomId}")
    public ResponseEntity<Long> getChatRoomKind(@PathVariable("chatRoomId") Long chatRoomId) {
        Optional<ChatRoomEntity> chatRoom = chatRoomRepository.findById(chatRoomId);
        if (chatRoom.isPresent()) {
            return ResponseEntity.ok(chatRoom.get().getChatRoomKind().getChatroomKindId()); // chatRoomKindId 반환
        }
        return ResponseEntity.notFound().build();
    }

    // 로그인한 사용자의 채팅방 목록 가져오기
    @GetMapping("/getChatRoomsByUser/{userId}")
    public ResponseEntity<List<ChatRoomDTO>> getChatRoomsByUser(@PathVariable("userId") String userId) {
        List<ChatRoomDTO> chatRoomDTOs = chatRoomService.getChatRoomsByUser(userId);
        if (chatRoomDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(chatRoomDTOs);
        }
    }
 // 사용자가 채팅방에서 나가는 기능 추가
    @DeleteMapping("/leave")
    public ResponseEntity<String> leaveChatRoom(@RequestBody ChatRoomInviteDTO chatRoomInviteDTO) {
        log.debug("사용자 채팅방 나가기 요청 - chatRoomId: {}, memberId: {}", chatRoomInviteDTO.getChatRoomId(), chatRoomInviteDTO.getMemberId());

        // 채팅방 나가기 로직 호출
        boolean success = chatRoomService.leaveChatRoom(chatRoomInviteDTO.getChatRoomId(), chatRoomInviteDTO.getMemberId());

        if (success) {
            return ResponseEntity.ok(chatRoomInviteDTO.getMemberId() + "님이 채팅방을 나갔습니다.");
        } else {
            return ResponseEntity.badRequest().body("채팅방 나가기에 실패했습니다.");
        }
    }

    // 채팅방 이름으로 채팅방 ID 가져오기
    @GetMapping("/getChatRoomIdByName/{chatRoomName}")
    public ResponseEntity<Long> getChatRoomIdByName(@PathVariable("chatRoomName") String chatRoomName) {
        ChatRoomEntity chatRoom = chatRoomRepository.findByChatRoomName(chatRoomName)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
        return ResponseEntity.ok(chatRoom.getChatRoomId());
    }

    // 새로운 채팅방 생성 (사용자가 생성하는 채팅방은 자동으로 "프로젝트" 종류로 설정)
    @PostMapping("/add")
    public ResponseEntity<Long> createRoom(@RequestBody ChatRoomDTO chatRoomDTO) {
        log.debug("새로운 채팅방 생성 요청: {}", chatRoomDTO.getChatRoomName());

        // 서비스에서 채팅방 생성 로직 호출 후 chatRoomId 반환
        Long chatRoomId = chatRoomService.createProjectChatRoom(chatRoomDTO);

        log.debug("채팅방 생성 완료: {}, chatRoomId: {}", chatRoomDTO.getChatRoomName(), chatRoomId);
        return ResponseEntity.ok(chatRoomId);  // 생성된 채팅방의 chatRoomId 반환
    }
    
    @GetMapping("/participants/{chatRoomId}")
    public ResponseEntity<List<MemberDTO>> getChatRoomParticipants(@PathVariable("chatRoomId") Long chatRoomId) {
        List<String> participantNames = chatRoomService.getChatRoomParticipants(chatRoomId);
        List<MemberDTO> participants = new ArrayList<>();

        for (String name : participantNames) {
            MemberDTO memberDTO = MemberDTO.builder()
                    .memberName(name)
                    .build();
            participants.add(memberDTO);
        }

        // 응답이 없을 때 빈 배열을 반환
        if (participants.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());  // 빈 배열 반환
        }

        return ResponseEntity.ok(participants);
    }




    // 채팅방에 사용자 초대
    @PostMapping("/invite")
    public ResponseEntity<String> inviteUserToChatRoom(@RequestBody ChatRoomInviteDTO chatRoomInviteDTO) {
        log.debug("사용자 초대 기능 호출 - chatRoomId: {}, memberId: {}", chatRoomInviteDTO.getChatRoomId(), chatRoomInviteDTO.getMemberId());

        // chatRoomId와 memberId로 채팅방 조회 및 초대 처리
        boolean success = chatRoomService.inviteUserToChatRoom(chatRoomInviteDTO.getChatRoomId(), chatRoomInviteDTO.getMemberId());

        if (success) {
            return ResponseEntity.ok(chatRoomInviteDTO.getMemberId() + "님이 채팅방에 초대되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("초대에 실패했습니다.");
        }
    }

 // 채팅방 삭제
    @Transactional
    @DeleteMapping("/delete/{chatRoomId}")
    public ResponseEntity<String> deleteChatRoom(@PathVariable("chatRoomId") Long chatRoomId) {
        log.debug("채팅방 삭제 요청 - chatRoomId: {}", chatRoomId);

        // chatRoomId로 채팅방 조회
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        // 채팅방에 연결된 project_member 데이터 삭제
        List<ProjectMemberEntity> projectMembers = projectMemberRepository.findByChatRoom_ChatRoomId(chatRoomId);
        if (!projectMembers.isEmpty()) {
            projectMemberRepository.deleteAll(projectMembers);
            log.debug("채팅방 {} 에 연결된 프로젝트 멤버들 삭제 완료", chatRoomId);
        }

        // 채팅방 삭제
        chatRoomRepository.delete(chatRoom);
        log.debug("채팅방 {} 삭제 완료", chatRoomId);

        return ResponseEntity.ok(chatRoomId + " 채팅방이 삭제되었습니다.");
    }
}
