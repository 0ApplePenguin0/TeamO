package com.example.workhive.controller.Approval;

import com.example.workhive.domain.dto.approval.FormTemplateDetailDTO;
import com.example.workhive.service.approval.FormTemplateService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/form-templates")
@RequiredArgsConstructor
public class FormTemplateApiController {
    private final FormTemplateService formTemplateService;

    @GetMapping("/{templateId}")
    public ResponseEntity<FormTemplateDetailDTO> getFormTemplate(@PathVariable Long templateId, HttpSession session) {
        Long companyId = (Long) session.getAttribute("companyId");
        FormTemplateDetailDTO template = formTemplateService.getFormTemplateDetail(templateId, companyId);
        return ResponseEntity.ok(template);
    }
}
