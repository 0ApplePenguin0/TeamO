package com.example.workhive.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;

/**
 * 회원정보 DTO
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDetailDTO {

    private int memberNum;
    private String memberId;
    private int departmentNum;
    private String companyUrl;
    private String memberStatus;
    private String profile;
    private LocalDate hireDate;
    private Optional<Integer> subdepNum; // Optional로 변경하여 null 값을 처리
}
