package com.example.workhive.service.approval;

import com.example.workhive.domain.dto.approval.FormTemplateDetailDTO;
import com.example.workhive.domain.dto.approval.UpdateFormTemplateRequestDTO;
import com.example.workhive.domain.entity.Approval.CompanyCustomTemplateEntity;
import com.example.workhive.domain.entity.Approval.CompanyCustomTemplateId;
import com.example.workhive.domain.entity.Approval.FormTemplateEntity;
import com.example.workhive.repository.Approval.CompanyCustomTemplateRepository;
import com.example.workhive.repository.Approval.FormTemplateRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FormTemplateService {
    private final FormTemplateRepository formTemplateRepository;
    private final CompanyCustomTemplateRepository companyCustomTemplateRepository;

    // 회사별 커스텀 양식 상세 조회 (커스텀 양식이 없으면 기본 양식 반환)
    public FormTemplateDetailDTO getCompanyFormTemplateDetail(Long templateId, Long companyId) {
        // 회사의 커스텀 양식 조회
        Optional<CompanyCustomTemplateEntity> customTemplateOpt =
                companyCustomTemplateRepository.findByCompanyIdAndTemplateIdAndIsActiveTrue(companyId, templateId);

        if (customTemplateOpt.isPresent()) {
            // 커스텀 양식이 있으면 해당 양식을 반환
            return new FormTemplateDetailDTO(customTemplateOpt.get());
        } else {
            // 없으면 기본 양식을 반환
            FormTemplateEntity formTemplate = formTemplateRepository.findById(templateId)
                    .orElseThrow(() -> new RuntimeException("기본 양식을 찾을 수 없습니다."));
            return new FormTemplateDetailDTO(formTemplate);
        }
    }

    // 회사별 양식 목록 조회
    public List<FormTemplateDetailDTO> getActiveFormTemplates(Long companyId) {
        List<FormTemplateEntity> defaultTemplates = formTemplateRepository.findByIsActiveTrue();
        List<CompanyCustomTemplateEntity> customTemplates =
                companyCustomTemplateRepository.findByCompanyIdAndIsActiveTrue(companyId);

        // 커스텀 양식을 맵으로 변환
        Map<Long, CompanyCustomTemplateEntity> customTemplateMap = customTemplates.stream()
                .collect(Collectors.toMap(CompanyCustomTemplateEntity::getTemplateId, Function.identity()));

        List<FormTemplateDetailDTO> templateDTOs = new ArrayList<>();
        for (FormTemplateEntity template : defaultTemplates) {
            if (customTemplateMap.containsKey(template.getTemplateId())) {
                // 커스텀 양식이 있으면 해당 양식을 사용
                templateDTOs.add(new FormTemplateDetailDTO(customTemplateMap.get(template.getTemplateId())));
            } else {
                // 없으면 기본 양식을 사용
                templateDTOs.add(new FormTemplateDetailDTO(template));
            }
        }
        return templateDTOs;
    }

    // 양식 수정 또는 생성 (회사별 커스텀 양식)
    public void updateFormTemplate(Long templateId, Long companyId, UpdateFormTemplateRequestDTO updateRequest) {

        CompanyCustomTemplateId id = new CompanyCustomTemplateId(companyId, templateId);
        CompanyCustomTemplateEntity customTemplate = companyCustomTemplateRepository.findById(id).orElse(null);

        if (customTemplate != null) {
            // 기존 커스텀 양식이 있으면 업데이트
            customTemplate.setCustomStructure(updateRequest.getFormStructure());
            customTemplate.setUpdatedAt(LocalDateTime.now());
            companyCustomTemplateRepository.save(customTemplate);
        } else {
            // 없으면 새로 생성 (기본 양식이 존재해야 함)
            FormTemplateEntity formTemplate = formTemplateRepository.findById(templateId)
                    .orElseThrow(() -> new RuntimeException("기본 양식을 찾을 수 없습니다."));
            customTemplate = CompanyCustomTemplateEntity.builder()
                    .companyId(companyId)
                    .templateId(templateId)
                    .customStructure(updateRequest.getFormStructure())
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .formTemplate(formTemplate)
                    .build();
            companyCustomTemplateRepository.save(customTemplate);
        }
    }

}
