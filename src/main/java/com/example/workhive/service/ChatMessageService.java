package com.example.workhive.service;


import com.example.workhive.domain.dto.ChatMessageDTO;
import com.example.workhive.domain.entity.ChatMessageEntity;
import com.example.workhive.domain.entity.ChatRoomEntity;
import com.example.workhive.repository.ChatMessageRepository;
import com.example.workhive.repository.ChatRoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    public ChatMessageService(ChatMessageRepository chatMessageRepository, ChatRoomRepository chatRoomRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    public List<ChatMessageDTO> getMessagesByChatRoom(Integer chatRoomId) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        List<ChatMessageEntity> messages = chatMessageRepository.findByChatRoom(chatRoom);
        return messages.stream()
                .map(message -> ChatMessageDTO.builder()
                        .messageId(message.getMessageId())
                        .chatRoomId(message.getChatRoom().getChatRoomId())
                        .senderId(message.getSenderId())
                        .messageContent(message.getMessageContent())
                        .sentAt(message.getSentAt())
                        .file(message.getFile())
                        .isNotice(message.isNotice())
                        .build())
                .collect(Collectors.toList());
    }

    public void saveMessage(ChatMessageDTO messageDTO) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(messageDTO.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        ChatMessageEntity message = new ChatMessageEntity();
        message.setChatRoom(chatRoom);
        message.setSenderId(messageDTO.getSenderId());
        message.setMessageContent(messageDTO.getMessageContent());
        message.setFile(messageDTO.getFile());
        message.setNotice(messageDTO.isNotice());

        chatMessageRepository.save(message);
    }
}
