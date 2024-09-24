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

    private Long memberDetailId;  // member_detail_id에 해당
    private String memberId;      // member_id에 해당
    private Long positionId;      // position_id에 해당
    private Long departmentId;    // department_id에 해당
    private Long teamId;          // team_id에 해당
    private String status;        // status에 해당 (퇴직, 출장, 출산휴가 등)
    private String profileUrl;    // profile_url에 해당
    private LocalDate hireDate;   // hire_date에 해당
}
