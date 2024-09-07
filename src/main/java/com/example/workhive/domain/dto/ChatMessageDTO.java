package com.example.workhive.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageDTO {
    private Long messageId;
    private Integer chatRoomId;
    private String senderId;
    private String messageContent;
    private LocalDateTime sentAt;
    private String file;
    private boolean isNotice;

    @Builder
    public ChatMessageDTO(Long messageId, Integer chatRoomId, String senderId, String messageContent, 
                          LocalDateTime sentAt, String file, boolean isNotice) {
        this.messageId = messageId;
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.messageContent = messageContent;
        this.sentAt = sentAt;
        this.file = file;
        this.isNotice = isNotice;
    }
}
