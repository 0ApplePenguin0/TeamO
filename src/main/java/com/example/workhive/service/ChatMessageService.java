package com.example.workhive.service;

import com.example.workhive.domain.dto.ChatMessageDTO;
import com.example.workhive.domain.entity.ChatMessageEntity;
import com.example.workhive.domain.entity.ChatRoomEntity;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.repository.ChatMessageRepository;
import com.example.workhive.repository.ChatRoomRepository;
import com.example.workhive.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
        MemberEntity member = memberRepository.findByMemberId(messageDTO.getMemberId());

        // ChatMessageEntity 생성
        ChatMessageEntity chatMessageEntity = new ChatMessageEntity();
        chatMessageEntity.setChatRoom(chatRoom);
        chatMessageEntity.setMember(member);
        chatMessageEntity.setMessage(messageDTO.getMessage());

        // 메시지 저장
        chatMessageRepository.save(chatMessageEntity);
    }

    // 특정 채팅방의 메시지 조회
    public List<ChatMessageDTO> getMessagesByChatRoom(Long chatRoomId) {
        List<ChatMessageEntity> messages = chatMessageRepository.findByChatRoom_ChatRoomIdAndIsDeletedFalse(chatRoomId);
        List<ChatMessageDTO> messageDTOs = new ArrayList<>();

        for (ChatMessageEntity message : messages) {
            ChatMessageDTO messageDTO = ChatMessageDTO.fromEntity(message);
            messageDTOs.add(messageDTO);
        }

        return messageDTOs;
    }
}
