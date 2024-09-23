package com.example.workhive.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long messageId;
    private String senderId;
    private String receiverId;
    private String title;
    private String content;
    private LocalDateTime sentAt;
    private Boolean isRead;
    private Boolean isDeleted;
    private LocalDateTime deleteDate;
}
