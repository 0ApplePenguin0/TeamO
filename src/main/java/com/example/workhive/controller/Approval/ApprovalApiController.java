    package com.example.workhive.controller.Approval;

    import com.example.workhive.domain.dto.DepartmentDTO;
    import com.example.workhive.domain.dto.MemberDTO;
    import com.example.workhive.domain.dto.TeamDTO;
    import com.example.workhive.security.AuthenticatedUser;
    import com.example.workhive.security.AuthenticatedUserDetailsService;
    import com.example.workhive.service.approval.ApprovalService;
    import jakarta.servlet.http.HttpSession;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    import java.util.List;

    @RestController
    @RequestMapping("/api/approvals")
    @RequiredArgsConstructor
    public class ApprovalApiController {
        private final ApprovalService approvalService;
        private final AuthenticatedUserDetailsService authenticatedUserDetailsService;

        /**
         * 회사의 부서 목록 조회
         */
        @GetMapping("/departments")
        public ResponseEntity<List<DepartmentDTO>> getDepartments(@AuthenticationPrincipal AuthenticatedUser user, HttpSession session) {
            Long companyId = (Long) session.getAttribute("companyId");
            List<DepartmentDTO> departments = approvalService.getDepartments(companyId);
            return ResponseEntity.ok(departments);
        }

        /**
         * 특정 부서의 팀 목록 조회
         */
        @GetMapping("/departments/{departmentId}/teams")
        public ResponseEntity<List<TeamDTO>> getTeams(@PathVariable Long departmentId) {
            List<TeamDTO> teams = approvalService.getTeamsByDepartment(departmentId);
            return ResponseEntity.ok(teams);
        }

        /**
         * 특정 팀의 멤버 목록 조회
         */
        @GetMapping("/teams/{teamId}/members")
        public ResponseEntity<List<MemberDTO>> getMembers(@PathVariable Long teamId) {
            List<MemberDTO> members = approvalService.getMembersByTeam(teamId);
            return ResponseEntity.ok(members);
        }
    }