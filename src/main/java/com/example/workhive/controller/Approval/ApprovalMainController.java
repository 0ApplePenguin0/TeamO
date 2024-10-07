package com.example.workhive.controller.Approval;

import com.example.workhive.domain.dto.approval.ApprovalDetailDTO;
import com.example.workhive.security.AuthenticatedUser;
import com.example.workhive.service.approval.ApprovalService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
@RequestMapping("/main")
public class ApprovalMainController {
    private final ApprovalService approvalService;

    public ApprovalMainController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @GetMapping("approval-board")
    public String showMainBoard(@AuthenticationPrincipal AuthenticatedUser user, Model model) {
        List<ApprovalDetailDTO> reportsToApprove = approvalService.getReportsToApprove(user.getMemberId());
        // 첫 5개의 보고서만 가져오기
        List<ApprovalDetailDTO> limitedReports = reportsToApprove.stream()
                .limit(5)
                .toList();
        model.addAttribute("reportsToApprove", limitedReports);
        return "main/board";
    }
}