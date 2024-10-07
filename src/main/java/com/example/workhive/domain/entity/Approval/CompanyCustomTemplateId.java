package com.example.workhive.domain.entity.Approval;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

//복합키 사용을 위한 클래스
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyCustomTemplateId implements Serializable {
    private Long companyId;
    private Long templateId;
}
