package com.example.workhive.service.approval;

import com.example.workhive.domain.dto.DepartmentDTO;
import com.example.workhive.domain.dto.MemberDTO;
import com.example.workhive.domain.dto.TeamDTO;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApprovalService {
    private final ApprovalRepository approvalRepository;
    private final ApprovalLineRepository approvalLineRepository;
    private final ApprovalHistoryRepository approvalHistoryRepository;
    private final FormTemplateRepository formTemplateRepository;
    private final MemberRepository memberRepository;
    private final CompanyRepository companyRepository;
    private final DepartmentRepository departmentRepository;
    private final TeamRepository teamRepository;

    private static final Logger logger = LoggerFactory.getLogger(ApprovalService.class);
    private final ObjectMapper objectMapper; // ObjectMapper 주입

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
    public List<DepartmentDTO> getDepartments(Long companyId) {
        List<DepartmentEntity> departments = departmentRepository.findByCompany_CompanyId(companyId);
        return departments.stream()
                .map(dept -> DepartmentDTO.builder()
                        .departmentId(dept.getDepartmentId())
                        .companyId(dept.getCompany().getCompanyId())
                        .departmentName(dept.getDepartmentName())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 특정 부서의 팀 목록 조회
     */
    @Transactional
    public List<TeamDTO> getTeamsByDepartment(Long departmentId) {
        List<TeamEntity> teams = teamRepository.findByDepartment_DepartmentId(departmentId);
        return teams.stream()
                .map(team -> TeamDTO.builder()
                        .teamId(team.getTeamId())
                        .departmentId(team.getDepartment().getDepartmentId())
                        .teamName(team.getTeamName())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 특정 팀의 멤버 목록 조회
     */
    @Transactional
    public List<MemberDTO> getMembersByTeam(Long teamId) {
        List<MemberEntity> members = memberRepository.findByMemberDetail_Team_TeamId(teamId);
        return members.stream()
                .map(member -> MemberDTO.builder()
                        .memberId(member.getMemberId())
                        .memberName(member.getMemberName())
                        .email(member.getEmail())
                        .role(member.getRole())
                        .companyId(member.getCompany().getCompanyId())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void createReport(Long companyId, String requesterId, ReportRequestDTO request) {
        // approvalLineMemberIds를 가져옵니다.
        List<String> approvalLineMemberIds = request.getApprovalLineMemberIds();
        if (approvalLineMemberIds == null || approvalLineMemberIds.isEmpty()) {
            throw new RuntimeException("결재 라인을 한 명 이상 지정해야 합니다.");
        }
        if (approvalLineMemberIds.size() > 3) {
            throw new RuntimeException("결재자는 최대 3명까지 지정할 수 있습니다.");
        }
        // 양식 템플릿 조회
        FormTemplateEntity formTemplate = formTemplateRepository.findById(request.getTemplateId())
                .orElseThrow(() -> new RuntimeException("Form template not found"));

        // 회사 조회
        CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        // 요청자 조회
        MemberEntity requester = memberRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("Requester not found"));

        // content 유효성 검증
        Map<String, Object> contentMap;
        try {
            logger.debug("체크 {}", request.getContent());
            // content가 Map이므로 별도의 직렬화가 필요 없음
            // JSON 유효성 검증을 위해 다시 직렬화 후 파싱 (optional)
            String contentJson = objectMapper.writeValueAsString(request.getContent());
            logger.debug("직렬화 JSON: {}", contentJson);
            objectMapper.readTree(contentJson); // 유효성 검증
            logger.debug("Content JSON is valid");

            contentMap = request.getContent();
        } catch (JsonProcessingException e) {
            logger.error("Invalid content JSON: {}", request.getContent(), e);
            throw new RuntimeException("Invalid content JSON", e);
        }

        // 결재 엔티티 생성
        ApprovalEntity approval = ApprovalEntity.builder()
                .formTemplate(formTemplate)
                .requester(requester)
                .company(company)
                .title(request.getTitle())
                .content(contentMap)
                .approvalStatus("PENDING")
                .requestDate(LocalDateTime.now())
                .build();

        approval = approvalRepository.save(approval);

        // 결재 라인 저장 (최대 3개)
        int stepOrder = 1;
        for (String approverId : approvalLineMemberIds) {
            MemberEntity approver = memberRepository.findById(approverId)
                    .orElseThrow(() -> new RuntimeException("Approver not found: " + approverId));

            ApprovalLineEntity approvalLine = ApprovalLineEntity.builder()
                    .approval(approval)
                    .member(approver)
                    .stepOrder(stepOrder++)
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
                    .title(approval.getTitle())
                    .content(approval.getContent())
                    .approvalStatus(approval.getApprovalStatus())
                    .requestDate(approval.getRequestDate())
                    .requesterName(approval.getRequester().getMemberName())
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
                    .content(approval.getContent())
                    .approvalStatus(approval.getApprovalStatus())
                    .requestDate(approval.getRequestDate())
                    .requesterName(approval.getRequester().getMemberName())
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
                .title(approval.getTitle())
                .content(approval.getContent())
                .approvalStatus(approval.getApprovalStatus())
                .requestDate(approval.getRequestDate())
                .requesterName(approval.getRequester().getMemberName())
                .formStructure(approval.getFormTemplate().getFormStructure())
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

    @Transactional
    public List<MemberEntity> getMembersByCompany(Long companyId) {
        return memberRepository.findByCompanyCompanyId(companyId);
    }

    @Transactional
    public void updateReport(Long reportId, String memberId, ApprovalDetailDTO reportDetailDTO) {
        // 보고서 조회
        ApprovalEntity approval = approvalRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("보고서를 찾을 수 없습니다."));

        // 수정 권한 확인
        if (!approval.getRequester().getMemberId().equals(memberId)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        // 내용 업데이트
        approval.setTitle(reportDetailDTO.getTitle());
        // content 유효성 검증 및 할당
        Map<String, Object> contentMap;
        try {
            // DTO의 content가 Map이므로 별도의 직렬화가 필요 없음
            // JSON 유효성 검증을 위해 직렬화 후 파싱 (optional)
            String contentJson = objectMapper.writeValueAsString(reportDetailDTO.getContent());
            objectMapper.readTree(contentJson); // 유효성 검증
            logger.debug("Updated content JSON: {}", contentJson);

            contentMap = reportDetailDTO.getContent();
        } catch (JsonProcessingException e) {
            logger.error("Invalid content JSON: {}", reportDetailDTO.getContent(), e);
            throw new RuntimeException("Invalid content JSON", e);
        }

        approval.setContent(contentMap);

        // 기타 필요한 필드 업데이트

        // 저장
        approvalRepository.save(approval);
    }

    /**
     * 전체 보고서 목록 조회
     */
    @Transactional
    public List<ApprovalDetailDTO> getAllReports(String memberId) {
        // 사용자의 소속 회사 ID 조회
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Long companyId = member.getCompany().getCompanyId();

        // 회사 내 모든 결재 조회
        List<ApprovalEntity> approvals = approvalRepository.findByCompany_CompanyId(companyId);

        return approvals.stream().map(approval -> {
            ApprovalDetailDTO detail = ApprovalDetailDTO.builder()
                    .approvalId(approval.getApprovalId())
                    .formName(approval.getFormTemplate().getFormName())
                    .title(approval.getTitle())
                    .content(approval.getContent())
                    .approvalStatus(approval.getApprovalStatus())
                    .requestDate(approval.getRequestDate())
                    .requesterName(approval.getRequester().getMemberName())
                    .formStructure(approval.getFormTemplate().getFormStructure())
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
}