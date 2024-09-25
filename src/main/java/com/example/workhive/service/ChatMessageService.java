package com.example.workhive.service;

import com.example.workhive.domain.dto.ChatMessageDTO;
import com.example.workhive.domain.entity.ChatMessageEntity;
import com.example.workhive.domain.entity.ChatRoomEntity;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.repository.ChatMessageRepository;
import com.example.workhive.repository.ChatRoomRepository;
import com.example.workhive.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    public ChatMessageService(ChatMessageRepository chatMessageRepository, ChatRoomRepository chatRoomRepository, MemberRepository memberRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.memberRepository = memberRepository;
    }

    public List<ChatMessageDTO> getMessagesByChatRoom(Long chatRoomId) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        List<ChatMessageEntity> messages = chatMessageRepository.findByChatRoom(chatRoom);
        List<ChatMessageDTO> messageDTOList = new ArrayList<>();

        for (ChatMessageEntity message : messages) {
            ChatMessageDTO messageDTO = ChatMessageDTO.builder()
                    .chatId(message.getChatId())  // chatId 필드 사용
                    .chatRoomId(message.getChatRoom().getChatRoomId())
                    .memberId(message.getMember().getMemberId())  // 발신자 정보
                    .message(message.getMessage())  // 메시지 내용 필드
                    .sentAt(message.getSentAt())
                    .isDeleted(message.isDeleted())  // 삭제 여부 필드
                    .build();

            messageDTOList.add(messageDTO);
        }

        return messageDTOList;
    }

    public void saveMessage(ChatMessageDTO messageDTO) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(messageDTO.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        MemberEntity member = memberRepository.findById(messageDTO.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        ChatMessageEntity message = new ChatMessageEntity();
        message.setChatRoom(chatRoom);
        message.setMember(member);  // 발신자 정보 설정
        message.setMessage(messageDTO.getMessage());  // 메시지 내용 설정
        message.setSentAt(messageDTO.getSentAt());
        message.setDeleted(messageDTO.isDeleted());  // 메시지 삭제 여부 설정

        chatMessageRepository.save(message);
    }
}
