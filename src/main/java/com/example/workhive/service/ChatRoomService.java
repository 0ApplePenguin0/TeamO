package com.example.workhive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.workhive.domain.dto.ChatRoomDTO;
import com.example.workhive.domain.entity.ChatRoomEntity;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.repository.ChatRoomRepository;
import com.example.workhive.repository.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 채팅방 서비스
 */
@Service
@RequiredArgsConstructor  // Lombok을 사용해 final 필드 자동 초기화
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    // 채팅방 목록 불러오기
    public List<ChatRoomDTO> getAllChatRooms() {
        List<ChatRoomEntity> rooms = chatRoomRepository.findAll();
        return rooms.stream()
                .map(room -> new ChatRoomDTO(
                        room.getChatRoomId(),          // 채팅방 ID
                        room.getCompanyUrl(),          // 회사 URL
                        room.getDepartmentId(),        // 부서 ID
                        room.getSubDepId(),            // 서브 부서 ID
                        room.getProjectNum(),          // 프로젝트 번호
                        room.getCreatedById(),         // 생성자 ID
                        room.getChatRoomName(),        // 채팅방 이름
                        room.getRemarks()))            // 비고
                .collect(Collectors.toList());
    }
    
 // 특정 사용자가 참여할 수 있는 채팅방 목록 불러오기
    public List<ChatRoomDTO> getChatRoomsByUserId(String userId) {
        List<ChatRoomEntity> rooms = chatRoomRepository.findByCreatedByIdOrInvitedUsers(userId);
        return rooms.stream()
                .map(room -> new ChatRoomDTO(
                        room.getChatRoomId(),
                        room.getCompanyUrl(),
                        room.getDepartmentId(),
                        room.getSubDepId(),
                        room.getProjectNum(),
                        room.getCreatedById(),
                        room.getChatRoomName(),
                        room.getRemarks()))
                .collect(Collectors.toList());
    }


    // 채팅방 생성하기
    public void createChatRoom(ChatRoomDTO chatRoomDTO) {
        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .companyUrl(chatRoomDTO.getCompanyUrl())
                .departmentId(chatRoomDTO.getDepartmentId())
                .subDepId(chatRoomDTO.getSubDepId())
                .projectNum(chatRoomDTO.getProjectNum())
                .createdById(chatRoomDTO.getCreatedById())
                .chatRoomName(chatRoomDTO.getChatRoomName())
                .remarks(chatRoomDTO.getRemarks())
                .build();
        chatRoomRepository.save(chatRoom);
    }

    // 채팅방 삭제하기
    public void deleteChatRoom(Integer chatRoomId) {
        chatRoomRepository.deleteById(chatRoomId);
        
    }
    
    public void inviteUserToChatRoom(String memberId, String roomName) {
        // 채팅방을 이름으로 검색하여 초대할 사용자 추가
        ChatRoomEntity chatRoom = chatRoomRepository.findByChatRoomName(roomName)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 없습니다."));

        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 없습니다."));

        chatRoom.getInvitedUsers().add(member);
        chatRoomRepository.save(chatRoom); // 변경 사항 저장
    }

}
		