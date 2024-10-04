package com.example.workhive.controller.Approval;

import com.example.workhive.domain.dto.approval.FormTemplateDetailDTO;
import com.example.workhive.domain.dto.approval.UpdateFormTemplateRequestDTO;
import com.example.workhive.security.AuthenticatedUser;
import com.example.workhive.service.approval.FormTemplateService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/form-templates")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminFormTemplateController {
    private final FormTemplateService formTemplateService;

    /**
     * 양식 템플릿 수정 폼
     */
    @GetMapping("/edit/{templateId}")
    public String showEditFormTemplate(@PathVariable Long templateId,
                                       @AuthenticationPrincipal AuthenticatedUser user,
                                       Model model,
                                       HttpSession session) {
        Long CompanyId = (Long) session.getAttribute("CompanyId");
        // 양식 템플릿 상세 조회
        FormTemplateDetailDTO template = formTemplateService.getFormTemplateDetail(templateId, CompanyId);
        model.addAttribute("template", template);
        model.addAttribute("updateRequest", new UpdateFormTemplateRequestDTO());
        return "admin/edit_form_template";
    }

    /**
     * 양식 템플릿 수정 처리
     */
    @PostMapping("/edit/{templateId}")
    public String updateFormTemplate(@PathVariable Long templateId,
                                     @ModelAttribute("updateRequest") UpdateFormTemplateRequestDTO updateRequest,
                                     BindingResult result,
                                     @AuthenticationPrincipal AuthenticatedUser user,
                                     HttpSession session,
                                     Model model) {
        Long companyId = (Long) session.getAttribute("CompanyId");
        if (result.hasErrors()) {
            FormTemplateDetailDTO template = formTemplateService.getFormTemplateDetail(templateId, companyId);
            model.addAttribute("template", template);
            return "admin/edit_form_template";
        }

        try {
            formTemplateService.updateFormTemplate(templateId, companyId, updateRequest);
            return "redirect:/form-templates";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "admin/edit_form_template";
        }
    }
}