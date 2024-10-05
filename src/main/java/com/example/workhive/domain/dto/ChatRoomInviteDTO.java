package com.example.workhive.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomInviteDTO {
    private Long chatRoomId;
    private String createdByMemberId;
    private String memberId;  // 초대할 사용자 ID
}	