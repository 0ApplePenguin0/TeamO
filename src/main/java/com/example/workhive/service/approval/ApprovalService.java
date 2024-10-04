package com.example.workhive.service.approval;

import com.example.workhive.domain.dto.approval.*;
import com.example.workhive.domain.entity.Approval.*;
import com.example.workhive.domain.entity.CompanyEntity;
import com.example.workhive.domain.entity.DepartmentEntity;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.domain.entity.TeamEntity;
import com.example.workhive.repository.Approval.*;
import com.example.workhive.repository.CompanyRepository;
import com.example.workhive.repository.DepartmentRepository;
import com.example.workhive.repository.MemberRepository;
import com.example.workhive.repository.TeamRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApprovalService {
    private final ApprovalRepository approvalRepository;
    private final ApprovalContentRepository approvalContentRepository;
    private final ApprovalLineRepository approvalLineRepository;
    private final ApprovalHistoryRepository approvalHistoryRepository;
    private final FormTemplateRepository formTemplateRepository;
    private final MemberRepository memberRepository;
    private final CompanyRepository companyRepository;
    private final DepartmentRepository departmentRepository;
    private final TeamRepository teamRepository;

    /**
     * 결재 승인
     */
    @Transactional
    public void approveApproval(ApprovalActionDTO.ApproveRequest request, String approverId) {
        ApprovalEntity approval = approvalRepository.findById(request.getApprovalId())
                .orElseThrow(() -> new RuntimeException("Approval not found"));

        // 전체 결재 상태 확인
        if (!approval.getApprovalStatus().equals("PENDING")) {
            throw new RuntimeException("Approval is not pending");
        }

        // 다음 결재 순서의 결재 라인 조회
        ApprovalLineEntity nextLine = approval.getApprovalLines().stream()
                .filter(line -> line.getStatus().equals("PENDING"))
                .sorted(Comparator.comparing(ApprovalLineEntity::getStepOrder))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No pending approval lines"));

        if (!nextLine.getMember().getMemberId().equals(approverId)) {
            throw new RuntimeException("It's not the approver's turn");
        }

        // 결재 라인 상태 업데이트
        nextLine.setStatus("APPROVED");
        nextLine.setComment(request.getComment());
        nextLine.setApprovalDate(LocalDateTime.now());
        approvalLineRepository.save(nextLine);

        // 결재 히스토리 저장
        ApprovalHistoryEntity history = ApprovalHistoryEntity.builder()
                .approval(approval)
                .member(nextLine.getMember())
                .status("APPROVED")
                .comment(request.getComment())
                .approvalDate(nextLine.getApprovalDate())
                .build();
        approvalHistoryRepository.save(history);

        // 모든 결재 라인이 승인되었는지 확인
        boolean allApproved = approval.getApprovalLines().stream()
                .allMatch(line -> line.getStatus().equals("APPROVED"));

        if (allApproved) {
            approval.setApprovalStatus("APPROVED");
            approvalRepository.save(approval);
        }
    }

    /**
     * 결재 거절
     */
    @Transactional
    public void rejectApproval(ApprovalActionDTO.RejectRequest request, String approverId) {
        ApprovalEntity approval = approvalRepository.findById(request.getApprovalId())
                .orElseThrow(() -> new RuntimeException("Approval not found"));

        // 전체 결재 상태 확인
        if (!approval.getApprovalStatus().equals("PENDING")) {
            throw new RuntimeException("Approval is not pending");
        }

        // 다음 결재 순서의 결재 라인 조회
        ApprovalLineEntity nextLine = approval.getApprovalLines().stream()
                .filter(line -> line.getStatus().equals("PENDING"))
                .sorted(Comparator.comparing(ApprovalLineEntity::getStepOrder))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No pending approval lines"));

        if (!nextLine.getMember().getMemberId().equals(approverId)) {
            throw new RuntimeException("It's not the approver's turn");
        }

        // 결재 라인 상태 업데이트
        nextLine.setStatus("REJECTED");
        nextLine.setComment(request.getComment());
        nextLine.setApprovalDate(LocalDateTime.now());
        approvalLineRepository.save(nextLine);

        // 결재 히스토리 저장
        ApprovalHistoryEntity history = ApprovalHistoryEntity.builder()
                .approval(approval)
                .member(nextLine.getMember())
                .status("REJECTED")
                .comment(request.getComment())
                .approvalDate(nextLine.getApprovalDate())
                .build();
        approvalHistoryRepository.save(history);

        // 전체 결재 상태 업데이트
        approval.setApprovalStatus("REJECTED");
        approvalRepository.save(approval);
    }

    /**
     * 부서 목록 조회
     */
    @Transactional
    public List<DepartmentEntity> getDepartments(Long companyId) {
        return departmentRepository.findByCompany_CompanyId(companyId);
    }

    /**
     * 특정 부서의 팀 목록 조회
     */
    @Transactional
    public List<TeamEntity> getTeamsByDepartment(Long departmentId) {
        return teamRepository.findByDepartmentDepartmentId(departmentId);
    }

    /**
     * 특정 팀의 멤버 목록 조회
     */
    @Transactional
    public List<MemberEntity> getMembersByTeam(Long teamId) {
        return memberRepository.findByMemberDetail_Team_TeamId(teamId);
    }

    @Transactional
    public void createReport(Long companyId, String requesterId, ReportRequestDTO request) {
        // 양식 템플릿 조회
        FormTemplateEntity formTemplate = formTemplateRepository.findById(request.getTemplateId())
                .orElseThrow(() -> new RuntimeException("Form template not found"));

        // 회사 조회
        CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        // 요청자 조회
        MemberEntity requester = memberRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("Requester not found"));

        // 결재 엔티티 생성
        ApprovalEntity approval = ApprovalEntity.builder()
                .formTemplate(formTemplate)
                .requester(requester)
                .company(company)
                .approvalStatus("PENDING")
                .requestDate(LocalDateTime.now())
                .build();

        approval = approvalRepository.save(approval);

        // 결재 내용 저장
        ApprovalContentEntity content = ApprovalContentEntity.builder()
                .approval(approval)
                .content(request.getContent())
                .build();
        approvalContentRepository.save(content);

        // 결재 라인 저장 (최대 3개)
        List<ApprovalLineDTO> approvalLines = request.getApprovalLines();
        if (approvalLines.size() > 3) {
            throw new RuntimeException("Maximum of 3 approval lines allowed");
        }

        for (ApprovalLineDTO lineDTO : approvalLines) {
            MemberEntity approver = memberRepository.findById(lineDTO.getMemberId())
                    .orElseThrow(() -> new RuntimeException("Approver not found: " + lineDTO.getMemberId()));

            DepartmentEntity department = departmentRepository.findById(lineDTO.getDepartmentId())
                    .orElse(null);

            TeamEntity team = teamRepository.findById(lineDTO.getTeamId())
                    .orElse(null);

            ApprovalLineEntity approvalLine = ApprovalLineEntity.builder()
                    .approval(approval)
                    .member(approver)
                    .department(department)
                    .team(team)
                    .stepOrder(lineDTO.getStepOrder())
                    .status("PENDING")
                    .build();

            approvalLineRepository.save(approvalLine);
        }
    }

    @Transactional
    public List<ApprovalDetailDTO> getMyReports(String memberId) {
        List<ApprovalEntity> approvals = approvalRepository.findByRequesterMemberId(memberId);

        return approvals.stream().map(approval -> {
            ApprovalDetailDTO detail = ApprovalDetailDTO.builder()
                    .approvalId(approval.getApprovalId())
                    .formName(approval.getFormTemplate().getFormName())
                    .content(approval.getContents().get(0).getContent())
                    .approvalStatus(approval.getApprovalStatus())
                    .requestDate(approval.getRequestDate())
                    .approvalLines(
                            approval.getApprovalLines().stream().map(line ->
                                    ApprovalLineDTO.builder()
                                            .approvalLineId(line.getApprovalLineId())
                                            .memberId(line.getMember().getMemberId())
                                            .memberName(line.getMember().getMemberName())
                                            .departmentId(line.getDepartment() != null ? line.getDepartment().getDepartmentId() : null)
                                            .departmentName(line.getDepartment() != null ? line.getDepartment().getDepartmentName() : null)
                                            .teamId(line.getTeam() != null ? line.getTeam().getTeamId() : null)
                                            .teamName(line.getTeam() != null ? line.getTeam().getTeamName() : null)
                                            .stepOrder(line.getStepOrder())
                                            .status(line.getStatus())
                                            .comment(line.getComment())
                                            .approvalDate(line.getApprovalDate())
                                            .build()
                            ).collect(Collectors.toList())
                    )
                    .build();
            return detail;
        }).collect(Collectors.toList());
    }

    @Transactional
    public List<ApprovalDetailDTO> getReportsToApprove(String memberId) {
        // 본인이 결재해야 할 결재 목록 조회
        List<ApprovalLineEntity> pendingLines = approvalLineRepository.findByMember_MemberIdAndStatus(memberId, "PENDING");

        // 결재 엔티티 리스트 추출
        List<ApprovalEntity> approvals = pendingLines.stream()
                .map(ApprovalLineEntity::getApproval)
                .distinct()
                .collect(Collectors.toList());

        return approvals.stream().map(approval -> {
            ApprovalDetailDTO detail = ApprovalDetailDTO.builder()
                    .approvalId(approval.getApprovalId())
                    .formName(approval.getFormTemplate().getFormName())
                    .content(approval.getContents().get(0).getContent())
                    .approvalStatus(approval.getApprovalStatus())
                    .requestDate(approval.getRequestDate())
                    .approvalLines(
                            approval.getApprovalLines().stream().map(line ->
                                    ApprovalLineDTO.builder()
                                            .approvalLineId(line.getApprovalLineId())
                                            .memberId(line.getMember().getMemberId())
                                            .memberName(line.getMember().getMemberName())
                                            .departmentId(line.getDepartment() != null ? line.getDepartment().getDepartmentId() : null)
                                            .departmentName(line.getDepartment() != null ? line.getDepartment().getDepartmentName() : null)
                                            .teamId(line.getTeam() != null ? line.getTeam().getTeamId() : null)
                                            .teamName(line.getTeam() != null ? line.getTeam().getTeamName() : null)
                                            .stepOrder(line.getStepOrder())
                                            .status(line.getStatus())
                                            .comment(line.getComment())
                                            .approvalDate(line.getApprovalDate())
                                            .build()
                            ).collect(Collectors.toList())
                    )
                    .build();
            return detail;
        }).collect(Collectors.toList());
    }

    @Transactional
    public ApprovalDetailDTO getReportDetail(Long approvalId, String memberId) {
        ApprovalEntity approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Approval not found"));

        // 접근 권한 체크 (요청자이거나 결재 라인에 포함된 경우)
        boolean isAuthorized = approval.getRequester().getMemberId().equals(memberId) ||
                approval.getApprovalLines().stream()
                        .anyMatch(line -> line.getMember().getMemberId().equals(memberId));

        if (!isAuthorized) {
            throw new RuntimeException("Access denied");
        }

        return ApprovalDetailDTO.builder()
                .approvalId(approval.getApprovalId())
                .formName(approval.getFormTemplate().getFormName())
                .content(approval.getContents().get(0).getContent())
                .approvalStatus(approval.getApprovalStatus())
                .requestDate(approval.getRequestDate())
                .approvalLines(
                        approval.getApprovalLines().stream().map(line ->
                                ApprovalLineDTO.builder()
                                        .approvalLineId(line.getApprovalLineId())
                                        .memberId(line.getMember().getMemberId())
                                        .memberName(line.getMember().getMemberName())
                                        .departmentId(line.getDepartment() != null ? line.getDepartment().getDepartmentId() : null)
                                        .departmentName(line.getDepartment() != null ? line.getDepartment().getDepartmentName() : null)
                                        .teamId(line.getTeam() != null ? line.getTeam().getTeamId() : null)
                                        .teamName(line.getTeam() != null ? line.getTeam().getTeamName() : null)
                                        .stepOrder(line.getStepOrder())
                                        .status(line.getStatus())
                                        .comment(line.getComment())
                                        .approvalDate(line.getApprovalDate())
                                        .build()
                        ).collect(Collectors.toList())
                )
                .build();
    }

    @Transactional
    public void approveReport(Long approvalId, String approverId, String comment) {
        ApprovalActionDTO.ApproveRequest request = ApprovalActionDTO.ApproveRequest.builder()
                .approvalId(approvalId)
                .comment(comment)
                .build();
        approveApproval(request, approverId);
    }

    @Transactional
    public void rejectReport(Long approvalId, String approverId, String comment) {
        ApprovalActionDTO.RejectRequest request = ApprovalActionDTO.RejectRequest.builder()
                .approvalId(approvalId)
                .comment(comment)
                .build();
        rejectApproval(request, approverId);
    }

}