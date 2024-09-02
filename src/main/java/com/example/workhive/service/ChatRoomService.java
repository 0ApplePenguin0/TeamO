package com.example.workhive.service;

import com.example.workhive.domain.dto.ChatRoomDTO;
import com.example.workhive.domain.entity.ChatRoomEntity;
import com.example.workhive.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 채팅방 서비스
 */
@RequiredArgsConstructor
@Service
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository repository;

    /* 채팅방 생성 */
    public void createChatRoom(ChatRoomDTO dto) {
        ChatRoomEntity entity = ChatRoomEntity.builder()
                .companyUrl(dto.getCompanyURL())
                .departmentId(dto.getDepartmentId())
                .departmentId(dto.getDepartmentId())
                .projectNum(dto.getProjectNum())
                .createdById(dto.getCreatedById())
                .chatRoomName(dto.getChatRoomName())
                .remarks(dto.getRemarks())
                .build();

        repository.save(entity);
    }
    
    /* 채팅방 나가기 */
    public void deleteChatRoom(Integer chatRoomId) {
        Optional<ChatRoomEntity> chatRoomOptional = repository.findById(chatRoomId);
        if (chatRoomOptional.isPresent()) {
        	repository.deleteById(chatRoomId);
        } else {
            throw new RuntimeException("채팅방을 찾을 수 없습니다.");
        }
    }
}
