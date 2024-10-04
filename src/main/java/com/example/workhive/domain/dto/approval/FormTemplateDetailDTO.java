package com.example.workhive.domain.dto.approval;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormTemplateDetailDTO {
    private Long templateId;
    private String formName;
    private String formStructure;
}