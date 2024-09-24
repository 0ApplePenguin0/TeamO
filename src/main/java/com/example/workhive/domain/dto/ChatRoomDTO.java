package com.example.workhive.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 채팅방 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDTO {

    private Long chatRoomId;                // 채팅방 ID (Primary Key)

    private Long chatRoomKindId;            // 채팅방 종류 ID (chatroom_kind 테이블 참조, Foreign Key)

    private Long companyId;                 // 회사 ID (company 테이블 참조, Foreign Key)

    private String createdByMemberId;       // 생성자 ID (members 테이블 참조, Foreign Key)

    private String chatRoomName;            // 채팅방 이름 (필수, 길이 50)
}
