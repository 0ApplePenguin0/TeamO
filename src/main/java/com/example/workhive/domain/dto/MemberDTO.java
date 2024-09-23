package com.example.workhive.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 회원정보 DTO
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {

    private String memberId;
    private String memberName;
    private String email;
    private String memberPassword; // 비밀번호는 보안 상 제외할 수도 있음
    private String role; // ENUM 형태로 관리할 경우
    private Long companyId;
}
