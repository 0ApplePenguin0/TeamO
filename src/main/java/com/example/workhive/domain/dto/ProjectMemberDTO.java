package com.example.workhive.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 프로젝트 멤버 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMemberDTO {
    
    private Long projectMemberId;   // 프로젝트 멤버 ID
    private Long chatRoomId;        // 채팅방 ID (Foreign Key)
    private String memberId;        // 멤버 ID (Foreign Key)
    private String role;            // 역할 (방장, 부방장, 일반 등)

}
