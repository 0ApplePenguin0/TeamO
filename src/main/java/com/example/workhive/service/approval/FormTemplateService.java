package com.example.workhive.service.approval;

import com.example.workhive.domain.dto.approval.FormTemplateDetailDTO;
import com.example.workhive.domain.dto.approval.UpdateFormTemplateRequestDTO;
import com.example.workhive.domain.entity.Approval.FormTemplateEntity;
import com.example.workhive.repository.Approval.FormTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FormTemplateService {
    private final FormTemplateRepository formTemplateRepository;

    public List<FormTemplateDetailDTO> getActiveFormTemplates(Long companyId) {
        List<FormTemplateEntity> templates = formTemplateRepository.findByCompanyCompanyIdAndIsActiveTrue(companyId);
        return templates.stream().map(template -> FormTemplateDetailDTO.builder()
                .templateId(template.getTemplateId())
                .formName(template.getFormName())
                .formStructure(template.getFormStructure())
                .build()).collect(Collectors.toList());
    }

    public FormTemplateDetailDTO getFormTemplateDetail(Long templateId, Long companyId) {
        FormTemplateEntity template = formTemplateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Form template not found"));

        if (!template.getCompany().getCompanyId().equals(companyId)) {
            throw new RuntimeException("Access denied");
        }

        return FormTemplateDetailDTO.builder()
                .templateId(template.getTemplateId())
                .formName(template.getFormName())
                .formStructure(template.getFormStructure())
                .build();
    }

    public void updateFormTemplate(Long templateId, Long companyId, UpdateFormTemplateRequestDTO request) {
        FormTemplateEntity template = formTemplateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Form template not found"));

        if (!template.getCompany().getCompanyId().equals(companyId)) {
            throw new RuntimeException("Access denied");
        }

    //    template.setFormStructure(request.getFormStructure());
        formTemplateRepository.save(template);
    }
}