package com.example.workhive.domain.dto;

import com.example.workhive.domain.entity.ChatMessageEntity;
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
    private String imageUrl;      // 이미지 URL 추가
    private LocalDateTime sentAt; // 메시지 전송 시간

    // ChatMessageEntity를 ChatMessageDTO로 변환하는 메서드
    public static ChatMessageDTO fromEntity(ChatMessageEntity entity) {
        return ChatMessageDTO.builder()
                .chatId(entity.getChatId())
                .chatRoomId(entity.getChatRoom().getChatRoomId())
                .memberId(entity.getMember().getMemberId())
                .message(entity.getMessage())
                .imageUrl(entity.getImageUrl()) // 이미지 URL 추가
                .sentAt(entity.getSentAt())
                .build();
    }
}
