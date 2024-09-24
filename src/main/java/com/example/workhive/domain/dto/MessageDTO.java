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
    private Long messageId;         // 메시지 ID (Primary Key)
    private String senderId;        // 발신자 ID (Foreign Key, members 테이블의 member_id)
    private String receiverId;      // 수신자 ID (Foreign Key, members 테이블의 member_id)
    private String title;           // 메시지 제목
    private String content;         // 메시지 내용
    private LocalDateTime sentAt;   // 메시지 전송 시간
    private boolean isRead;         // 메시지 읽음 여부
}
