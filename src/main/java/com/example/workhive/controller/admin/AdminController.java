package com.example.workhive.controller.admin;

import com.example.workhive.domain.dto.MemberDetailDTO;
import com.example.workhive.domain.entity.*;
import com.example.workhive.repository.*;
import com.example.workhive.security.AuthenticatedUser;
import com.example.workhive.service.admin.AdminService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("admin" )
@RequiredArgsConstructor
public class AdminController {
    private final MemberRepository usersRepository;
    private final DepartmentRepository departmentRepository;
    private final TeamRepository teamRepository;
    private final MemberDetailRepository memberDetailRepository;
    private final CompanyRepository companyRepository;
    private final PositionRepository positionRepository;

    private final AdminService adminService;


    @Value("${admin.pageSize}")
    int pageSize;

    @Value("${admin.linkSize}")
    int linkSize;

    @GetMapping("EmployeeList")
    public String employeelist(Model model
            , @RequestParam(name = "page", defaultValue = "1") int page
            , @RequestParam(name = "searchType", defaultValue = "") String searchType
            , @RequestParam(name = "searchWord", defaultValue = "") String searchWord
            , @RequestParam(name = "searchEnabled", defaultValue = "false") boolean searchEnabled
            , @AuthenticationPrincipal AuthenticatedUser user) {

        String loggedInUserId = user.getMemberId();

        // Member 테이블에서 companyId 가져오기
        MemberEntity member = usersRepository.findByMemberId(loggedInUserId);

        Long companyId = member.getCompany().getCompanyId();

        // 직원 목록 1페이지 가져오기
        List<Map<String, String>> memberList = adminService.getMembersByCompanyId(companyId, searchType, searchWord, searchEnabled);

        // 페이지 처리 추가
        int totalMembers = memberList.size();
        int totalPages = (int) Math.ceil((double) totalMembers / pageSize);

        // 현재 페이지의 인덱스 계산
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalMembers);



        List<Map<String, String>> memberPage = memberList.subList(fromIndex, toIndex);

        model.addAttribute("memberPage", memberPage);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchWord", searchWord);
        model.addAttribute("linkSize", linkSize);
        model.addAttribute("searchEnabled", searchEnabled);
        return "admin/EmployeeList";
    }

    @GetMapping("ReviseApproval")
    public String reviseapproval() {
        // 쪽지함 뷰로 이동
        return "admin/ReviseApproval";
    }

    @GetMapping("ReviseDivision")
    public String revisedivision(@AuthenticationPrincipal AuthenticatedUser user, Model model) {
        String loggedInUserId = user.getMemberId();
        MemberEntity member = usersRepository.findByMemberId(loggedInUserId);
        Long companyId = member.getCompany().getCompanyId();

        List<DepartmentEntity> deptEntity = departmentRepository.findByCompany_CompanyId(companyId);

        Map<DepartmentEntity, List<TeamEntity>> departmentTeamsMap = new HashMap<>();
        for (DepartmentEntity department : deptEntity) {
            List<TeamEntity> teams = adminService.getTeamsByDepartmentId(department.getDepartmentId());
            departmentTeamsMap.put(department, teams);
        }

        List<PositionEntity> posiEntity = positionRepository.findByCompany_CompanyId(companyId);

        model.addAttribute("departments", deptEntity);
        model.addAttribute("positions", posiEntity);
        model.addAttribute("departmentTeamsMap", departmentTeamsMap);


        return "admin/ReviseDivision";
    }

        @PostMapping("updateCompany")
        public String updateCompany(@RequestParam Map<String, String> companyData,
                                    RedirectAttributes redirectAttributes,
                                    Model model,
                                    HttpSession session,
                                    @AuthenticationPrincipal AuthenticatedUser user) {
            // 현재 로그인한 사용자 ID 가져오기
            String loggedInUserId = user.getMemberId();
            System.out.println("확인 ok companyData: " + companyData);

            companyData.put("memberId", loggedInUserId);


            boolean isUpdated = adminService.updateCompanyWithDepartments(companyData);

            if (isUpdated) {
                redirectAttributes.addFlashAttribute("successMessage", "회사가 성공적으로 업데이트되었습니다.");
                return "redirect:/admin/ReviseDivision"; // 성공 시 리다이렉트
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "회사 업데이트 실패");
                return "redirect:/admin/ReviseDivision"; // 실패 시에도 리다이렉트, 에러 메시지를 전달
            }
        }

    @GetMapping("EmployeeEdit")
    public String editEmployee(@RequestParam String memberId, Model model) {
        MemberEntity member = usersRepository.findByMemberId(memberId);
        Long companyId = member.getCompany().getCompanyId();
        MemberEntity.RoleEnum role = member.getRole();
        model.addAttribute("companyId", companyId);
        model.addAttribute("memberId", memberId);
        model.addAttribute("role", role);

        return "admin/EmployeeEdit"; // 수정 페이지 반환
    }

    @GetMapping("FindMember")
    @ResponseBody
    public ResponseEntity<MemberDetailDTO> getMemberDetails(@RequestParam String memberId, Model model) {
        MemberDetailEntity memberDetail = memberDetailRepository.findByMember_MemberId(memberId);
        MemberEntity member = usersRepository.findByMemberId(memberId);

        MemberEntity.RoleEnum role = member.getRole();
        model.addAttribute("role", role);
        MemberDetailDTO memberDetailDTO = MemberDetailDTO.builder()
                .memberDetailId(memberDetail.getMemberDetailId()) // 회원 상세 ID
                .memberId(memberDetail.getMember().getMemberId()) // 회원 ID
                .positionId(memberDetail.getPosition().getPositionId()) // 직급 ID
                .departmentId(memberDetail.getTeam().getDepartment().getDepartmentId()) // 부서 ID
                .teamId(memberDetail.getTeam().getTeamId()) // 팀 ID
                .status(memberDetail.getStatus()) // 회원 상태
                .companyId(member.getCompany().getCompanyId())
                .build();

        return ResponseEntity.ok(memberDetailDTO);
    }

    @PostMapping("updateMember")
    public String updateMember(@ModelAttribute MemberDetailDTO memberDetailDTO, String role) {

        adminService.updateMember(memberDetailDTO, role);
        // 수정 후 리다이렉트 또는 다른 페이지로 이동
        return "redirect:EmployeeList";
    }

    @GetMapping("deleteTeam")
    public String delete(@RequestParam("teamId") Long teamId) {
        try {
            // 쪽지 삭제 서비스 호출
            adminService.deleteTeam(teamId);
            // 성공 시 쪽지함 페이지로 이동
            return "redirect:/admin/ReviseDivision";
        } catch (Exception e) {
            // 예외 발생 시 스택 트레이스 출력
            e.printStackTrace();
            // 실패 시 쪽지 작성 폼으로 이동
            return "redirect:/admin/ReviseDivision";
        }
    }

}





