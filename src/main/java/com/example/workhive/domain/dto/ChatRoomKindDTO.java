package com.example.workhive.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 채팅방 종류 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomKindDTO {

    private Long chatroomKindId;   // 채팅방 종류 ID (Primary Key)
    private String kind;           // 종류 (회사, 부서, 팀, 프로젝트 등)
}
