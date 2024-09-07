package com.example.workhive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.workhive.domain.dto.ChatRoomDTO;
import com.example.workhive.domain.entity.ChatRoomEntity;
import com.example.workhive.repository.ChatRoomRepository;

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
    public void deleteChatRoom(int chatRoomId) {
        if (!chatRoomRepository.existsById(chatRoomId)) {
            throw new IllegalArgumentException("해당 채팅방을 찾을 수 없습니다.");
        }
        chatRoomRepository.deleteById(chatRoomId);
    }
}
		