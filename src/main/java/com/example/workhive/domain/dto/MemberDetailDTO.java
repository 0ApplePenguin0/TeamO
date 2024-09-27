package com.example.workhive.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 회원 상세 정보 DTO
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDetailDTO {

    private Long memberDetailId; // 회원 상세 ID
    private String memberId; // 회원 ID (MemberEntity의 ID)
    private Long positionId; // 직급 ID
    private Long departmentId; // 부서 ID
    private Long teamId; // 팀 ID
    private String status; // 회원 상태 (예: 재직 중, 퇴직 등)
    private String profileUrl; // 프로필 이미지 URL
    private LocalDate hireDate; // 입사 날짜
    private String memberName; // 회원 이름 (MemberEntity에서 가져옴)
    private Long companyId; // 회사 ID (MemberEntity에서 가져옴)
}
