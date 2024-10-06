package com.example.workhive.domain.dto.approval;

import com.example.workhive.domain.entity.Approval.CompanyCustomTemplateEntity;
import com.example.workhive.domain.entity.Approval.FormTemplateEntity;
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

    // 기본 양식용 생성자
    public FormTemplateDetailDTO(FormTemplateEntity formTemplate) {
        this.templateId = formTemplate.getTemplateId();
        this.formName = formTemplate.getFormName();
        this.formStructure = formTemplate.getFormStructure();
    }

    // 커스텀 양식용 생성자
    public FormTemplateDetailDTO(CompanyCustomTemplateEntity customTemplate) {
        this.templateId = customTemplate.getTemplateId();
        this.formName = customTemplate.getFormTemplate().getFormName(); // 기본 양식의 이름 사용
        this.formStructure = customTemplate.getCustomStructure();
    }
}