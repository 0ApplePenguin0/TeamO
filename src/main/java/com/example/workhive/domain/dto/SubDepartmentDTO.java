package com.example.workhive.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubDepartmentDTO {
    private int subdepNum;
    private String companyUrl;
    private int departmentNum;
    private String subdepName;
}
