package com.example.workhive.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 채팅룸 정보 DTO
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {

    private Integer  chatRoomId;       // 채팅룸 ID
    private  String  companyURL;        // 회사 URL
    private  String  departmentId;      // 부서 ID (Nullable)
    private  String  subDepId;          // 서브 부서 ID (Nullable)
    private  String  projectNum;        // 프로젝트 번호 (Nullable)
    private  String  createdById;       // 생성자 ID (회원 ID)
    private  String  chatRoomName;      // 채팅룸 이름
    private  String  remarks;           // 비고

}
