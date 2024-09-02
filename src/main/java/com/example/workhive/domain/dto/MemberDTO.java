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
    private String memberPassword;
    private String memberName;
    private String email;
    private String rolename;

}
