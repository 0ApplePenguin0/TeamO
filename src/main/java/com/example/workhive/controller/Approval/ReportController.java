package com.example.workhive.controller.Approval;

import com.example.workhive.domain.dto.DepartmentDTO;
import com.example.workhive.domain.dto.approval.ApprovalDetailDTO;
import com.example.workhive.domain.dto.approval.FormTemplateDetailDTO;
import com.example.workhive.domain.dto.approval.ReportRequestDTO;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.security.AuthenticatedUser;
import com.example.workhive.service.approval.ApprovalService;
import com.example.workhive.service.approval.FormTemplateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ApprovalService approvalService;
    private final FormTemplateService formTemplateService;
    private final HttpSession httpSession;
    private final ObjectMapper objectMapper;

    /**
     * 보고서 작성 폼
     */
    @GetMapping("/create")
    public String showCreateReportForm(@AuthenticationPrincipal AuthenticatedUser user, Model model, HttpSession session) {
        Long companyId = (Long) session.getAttribute("companyId");
        if (companyId == null) {
            throw new RuntimeException("CompanyId not found in session");
        }
        // 양식 템플릿 목록 조회
        List<FormTemplateDetailDTO> formTemplates = formTemplateService.getActiveFormTemplates(companyId);
        // 부서 목록 조회
        List<DepartmentDTO> departments = approvalService.getDepartments(companyId);
        // 멤버 목록 조회
        List<MemberEntity> members = approvalService.getMembersByCompany(companyId);

        model.addAttribute("formTemplates", formTemplates);
        model.addAttribute("departments", departments);
        model.addAttribute("reportRequest", new ReportRequestDTO()); // 모델에 reportRequest 추가
        return "approval/create_report";
    }

    /**
     * 보고서 생성 처리
     */
    @PostMapping("/create")
    public String createReport(@AuthenticationPrincipal AuthenticatedUser user,
                               @RequestParam("formContent") String formContent,
                               @ModelAttribute("reportRequest") ReportRequestDTO reportRequest,
                               BindingResult result,
                               HttpSession session,
                               Model model) {
        Long companyId = (Long) session.getAttribute("companyId");
        log.debug("폼 컨텐츠 출력: {}", formContent);

        // formContent을 content 필드로 설정
        reportRequest.setContent(formContent);

        log.debug("ReportRequestDTO 값: {}", reportRequest);
        log.debug("result binding : {}" ,result.getFieldValue("content"));
        // 디버깅을 위해 데이터 출력
        System.out.println("템플릿 ID: " + reportRequest.getTemplateId());
        System.out.println("폼 데이터: " + reportRequest.getContent());
        System.out.println("결재선 멤버 ID: " + reportRequest.getApprovalLineMemberIds());

        if (result.hasErrors()) {

            result.getAllErrors().forEach(e ->{
                System.out.println("error : " + e.getDefaultMessage());
            });

            model.addAttribute("formTemplates", formTemplateService.getActiveFormTemplates(companyId));
            model.addAttribute("departments", approvalService.getDepartments(companyId));
            return "approval/create_report";
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
        return "approval/my_reports";
    }

    /**
     * 결재해야 할 보고서 목록 조회
     */
    @GetMapping("/for-me")
    public String getReportsToApprove(@AuthenticationPrincipal AuthenticatedUser user, Model model) {
        List<ApprovalDetailDTO> reports = approvalService.getReportsToApprove(user.getMemberId());
        model.addAttribute("reports", reports);
        return "approval/reports_for_me";
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
        return "approval/report_detail";
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

    // 수정 페이지 표시
    @GetMapping("/edit/{reportId}")
    public String showEditReportForm(@PathVariable Long reportId,
                                     @AuthenticationPrincipal AuthenticatedUser user,
                                     Model model) {
        ApprovalDetailDTO reportDetail = approvalService.getReportDetail(reportId, user.getMemberId());
        model.addAttribute("reportDetail", reportDetail);
        return "approval/edit_report";
    }

    // 수정 처리
    @PostMapping("/edit/{reportId}")
    public String updateReport(@PathVariable Long reportId,
                               @AuthenticationPrincipal AuthenticatedUser user,
                               @RequestParam("formContent") String formContent,
                               @ModelAttribute("reportDetail") ApprovalDetailDTO reportDetailDTO,
                               BindingResult result,
                               Model model) {

        reportDetailDTO.setContent(formContent);
        log.debug("ReportDetailDTO 값: {}", reportDetailDTO);

        if (result.hasErrors()) {
            model.addAttribute("reportDetail", reportDetailDTO);
            return "approval/edit_report";
        }
        approvalService.updateReport(reportId, user.getMemberId(), reportDetailDTO);
        return "redirect:/reports/" + reportId;
    }

    /**
     * 전체 보고서 목록 조회
     */
    @GetMapping("/all")
    public String getAllReports(@AuthenticationPrincipal AuthenticatedUser user, Model model) {
        List<ApprovalDetailDTO> allReports = approvalService.getAllReports(user.getMemberId());
        model.addAttribute("allReports", allReports);
        return "approval/all_reports";
    }

}
