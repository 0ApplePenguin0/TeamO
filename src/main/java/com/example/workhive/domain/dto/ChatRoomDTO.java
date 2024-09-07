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

    private Integer chatRoomId;     // 채팅방 ID
    private String companyUrl;      // 회사 URL
    private String departmentId;    // 부서 ID
    private String subDepId;        // 서브 부서 ID
    private String projectNum;      // 프로젝트 번호
    private String createdById;     // 생성자 ID
    private String chatRoomName;    // 채팅방 이름
    private String remarks;         // 비고
}
