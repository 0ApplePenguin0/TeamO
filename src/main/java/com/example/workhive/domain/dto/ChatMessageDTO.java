package com.example.workhive.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private Long chatId;          // 메시지 ID (Primary Key)
    private Long chatRoomId;      // 채팅방 ID (Foreign Key)
    private String memberId;      // 발신자 ID (Foreign Key, members 테이블의 member_id)
    private String message;       // 메시지 내용
    private LocalDateTime sentAt; // 메시지 전송 시간
    private boolean isDeleted;    // 메시지 삭제 여부
}
