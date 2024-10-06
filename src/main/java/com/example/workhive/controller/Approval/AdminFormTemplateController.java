package com.example.workhive.controller.Approval;

import com.example.workhive.domain.dto.approval.FormTemplateDetailDTO;
import com.example.workhive.domain.dto.approval.UpdateFormTemplateRequestDTO;
import com.example.workhive.security.AuthenticatedUser;
import com.example.workhive.service.approval.FormTemplateService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/form-templates")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminFormTemplateController {
    private final FormTemplateService formTemplateService;

    /**
     * 양식 템플릿 목록 조회
     */
    @GetMapping
    public String listFormTemplates(@AuthenticationPrincipal AuthenticatedUser user, Model model, HttpSession session) {
        Long companyId = (Long) session.getAttribute("companyId");
        if (companyId == null) {
            throw new RuntimeException("companyId not found in session");
        }
        // 회사별 활성화된 양식 목록 조회
        List<FormTemplateDetailDTO> formTemplates = formTemplateService.getActiveFormTemplates(companyId);
        model.addAttribute("formTemplates", formTemplates);
        return "approval/form_template_list";
    }

    /**
     * 양식 템플릿 수정 폼
     */
    @GetMapping("/edit/{templateId}")
    public String showEditFormTemplate(@PathVariable Long templateId,
                                       @AuthenticationPrincipal AuthenticatedUser user,
                                       Model model,
                                       HttpSession session) {
        Long companyId = (Long) session.getAttribute("companyId");
        if (companyId == null) {
            throw new RuntimeException("companyId not found in session");
        }
        // 회사별 커스텀 양식 상세 조회
        FormTemplateDetailDTO template = formTemplateService.getCompanyFormTemplateDetail(templateId, companyId);
        model.addAttribute("template", template);
        model.addAttribute("updateRequest", new UpdateFormTemplateRequestDTO());
        return "approval/edit_form_template";
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

        log.debug("받은 json: {}", updateRequest.getFormStructure());


        Long companyId = (Long) session.getAttribute("companyId");
        if (companyId == null) {
            throw new RuntimeException("companyId not found in session");
        }
        if (result.hasErrors()) {
            FormTemplateDetailDTO template = formTemplateService.getCompanyFormTemplateDetail(templateId, companyId);
            model.addAttribute("template", template);
            return "approval/edit_form_template";
        }

        try {
            formTemplateService.updateFormTemplate(templateId, companyId, updateRequest);
            return "redirect:/admin/form-templates";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            FormTemplateDetailDTO template = formTemplateService.getCompanyFormTemplateDetail(templateId, companyId);
            model.addAttribute("template", template);
            return "approval/edit_form_template";
        }
    }
}