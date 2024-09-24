package com.example.workhive.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {
    
    private Long departmentId;     // 부서 ID (BIGINT에 맞게 수정)
    
    private Long companyId;        // 회사 ID (BIGINT에 맞게 수정)
    
    private String departmentName; // 부서 이름
}
