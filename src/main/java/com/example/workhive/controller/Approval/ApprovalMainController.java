package com.example.workhive.controller.Approval;

import com.example.workhive.domain.dto.approval.ApprovalDetailDTO;
import com.example.workhive.security.AuthenticatedUser;
import com.example.workhive.service.approval.ApprovalService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Controller
@RequestMapping("/main")
public class ApprovalMainController {
    private final ApprovalService approvalService;

    public ApprovalMainController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @GetMapping("approval-board")
    public String showMainBoard(@AuthenticationPrincipal AuthenticatedUser user, Model model) {
        // 사용자가 보낸 결재 가져오기
        List<ApprovalDetailDTO> sentReports = approvalService.getAllReports(user.getMemberId());
        List<ApprovalDetailDTO> receivedReports = approvalService.getReportsToApprove(user.getMemberId());

        // 로그 출력으로 데이터 확인
        System.out.println("보낸 결재: " + sentReports.size());
        System.out.println("받은 결재: " + receivedReports.size());

        // 보낸 결재와 받은 결재를 합쳐서 최신 순으로 정렬 후 5개만 선택
        List<ApprovalDetailDTO> combinedReports = Stream.concat(sentReports.stream(), receivedReports.stream())
                .sorted(Comparator.comparing(ApprovalDetailDTO::getRequestDate).reversed())
                .limit(5)
                .collect(Collectors.toList());

        System.out.println("전체 결재: " + combinedReports.size());

        model.addAttribute("reports", combinedReports);

        return "main/board";
    }
}