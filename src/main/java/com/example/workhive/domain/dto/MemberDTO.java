package com.example.workhive.domain.dto;

import com.example.workhive.domain.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {

    private String memberId;
    private String email;
    private String memberName;
    private String memberPassword;
    private MemberEntity.RoleEnum role;
    private Long companyId; 
}
