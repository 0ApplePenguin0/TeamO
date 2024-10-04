package com.example.workhive.controller.Approval;

import com.example.workhive.domain.dto.approval.ApprovalDetailDTO;
import com.example.workhive.domain.dto.approval.FormTemplateDetailDTO;
import com.example.workhive.domain.dto.approval.ReportRequestDTO;
import com.example.workhive.domain.entity.DepartmentEntity;
import com.example.workhive.security.AuthenticatedUser;
import com.example.workhive.security.AuthenticatedUserDetailsService;
import com.example.workhive.service.approval.ApprovalService;
import com.example.workhive.service.approval.FormTemplateService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ApprovalService approvalService;
    private final FormTemplateService formTemplateService;
    private final AuthenticatedUserDetailsService authenticatedUserDetailsService;
    private final HttpSession httpSession;

    /**
     * 보고서 작성 폼
     */
    @GetMapping("/create")
    public String showCreateReportForm(@AuthenticationPrincipal AuthenticatedUser user, Model model, HttpSession session) {
        Long companyId = (Long) session.getAttribute("CompanyId");
        // 양식 템플릿 목록 조회
        List<FormTemplateDetailDTO> formTemplates = formTemplateService.getActiveFormTemplates(companyId);
        // 부서 목록 조회
        List<DepartmentEntity> departments = approvalService.getDepartments(companyId);

        model.addAttribute("formTemplates", formTemplates);
        model.addAttribute("departments", departments);
        model.addAttribute("reportRequest", new ReportRequestDTO()); // 모델에 reportRequest 추가
        return "report/create_report";
    }

    /**
     * 보고서 생성 처리
     */
    @PostMapping("/create")
    public String createReport(@AuthenticationPrincipal AuthenticatedUser user,
                               @ModelAttribute("reportRequest") ReportRequestDTO reportRequest,
                               BindingResult result,
                               HttpSession session,
                               Model model) {
        Long companyId = (Long) session.getAttribute("CompanyId");

        if (result.hasErrors()) {
            model.addAttribute("formTemplates", formTemplateService.getActiveFormTemplates(companyId));
            model.addAttribute("departments", approvalService.getDepartments(companyId));
            return "report/create_report";
        }

        // 보고서 생성 로직 구현
        approvalService.createReport(companyId, user.getMemberId(), reportRequest);

        return "redirect:/reports/my";
    }

    /**
     * 내 보고서 목록 조회
     */
    @GetMapping("/my")
    public String getMyReports(@AuthenticationPrincipal AuthenticatedUser user, Model model) {
        List<ApprovalDetailDTO> reports = approvalService.getMyReports(user.getMemberId());
        model.addAttribute("reports", reports);
        return "report/my_reports";
    }

    /**
     * 결재해야 할 보고서 목록 조회
     */
    @GetMapping("/for-me")
    public String getReportsToApprove(@AuthenticationPrincipal AuthenticatedUser user, Model model) {
        List<ApprovalDetailDTO> reports = approvalService.getReportsToApprove(user.getMemberId());
        model.addAttribute("reports", reports);
        return "report/reports_for_me";
    }

    /**
     * 보고서 상세 조회
     */
    @GetMapping("/{reportId}")
    public String getReportDetail(@PathVariable Long reportId,
                                  @AuthenticationPrincipal AuthenticatedUser user,
                                  Model model) {
        ApprovalDetailDTO reportDetail = approvalService.getReportDetail(reportId, user.getMemberId());
        model.addAttribute("reportDetail", reportDetail);
        return "report/report_detail";
    }

    /**
     * 보고서 승인 처리
     */
    @PostMapping("/{reportId}/approve")
    public String approveReport(@PathVariable Long reportId,
                                @AuthenticationPrincipal AuthenticatedUser user,
                                @RequestParam String comment) {
        approvalService.approveReport(reportId, user.getMemberId(), comment);
        return "redirect:/reports/for-me";
    }

    /**
     * 보고서 거절 처리
     */
    @PostMapping("/{reportId}/reject")
    public String rejectReport(@PathVariable Long reportId,
                               @AuthenticationPrincipal AuthenticatedUser user,
                               @RequestParam String comment) {
        approvalService.rejectReport(reportId, user.getMemberId(), comment);
        return "redirect:/reports/for-me";
    }
}
