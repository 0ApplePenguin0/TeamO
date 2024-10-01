package com.example.workhive.service;

import com.example.workhive.domain.dto.ChatMessageDTO;
import com.example.workhive.domain.entity.ChatMessageEntity;
import com.example.workhive.domain.entity.ChatRoomEntity;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.repository.ChatMessageRepository;
import com.example.workhive.repository.ChatRoomRepository;
import com.example.workhive.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    // 메시지 저장
    @Transactional
    public void saveMessage(ChatMessageDTO messageDTO) {
        // 채팅방과 멤버를 가져옴
        ChatRoomEntity chatRoom = chatRoomRepository.findById(messageDTO.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 존재하지 않습니다."));
        MemberEntity member = memberRepository.findByMemberId(messageDTO.getMemberId());

        if (member == null) {
            throw new IllegalArgumentException("해당 사용자를 찾을 수 없습니다.");
        }

        // ChatMessageEntity 생성 및 데이터 세팅
        ChatMessageEntity chatMessageEntity = ChatMessageEntity.builder()
                .chatRoom(chatRoom)
                .member(member)
                .message(messageDTO.getMessage())
                .build();

        // 메시지 저장
        chatMessageRepository.save(chatMessageEntity);
    }

    // 특정 채팅방의 메시지 조회
    public List<ChatMessageDTO> getMessagesByChatRoom(Long chatRoomId) {
        List<ChatMessageEntity> messages = chatMessageRepository.findByChatRoom_ChatRoomIdAndIsDeletedFalse(chatRoomId);
        List<ChatMessageDTO> messageDTOs = new ArrayList<>();

        for (ChatMessageEntity message : messages) {
            // Entity를 DTO로 변환
            ChatMessageDTO messageDTO = ChatMessageDTO.fromEntity(message);
            messageDTOs.add(messageDTO);
        }

        return messageDTOs;
    }
}
