package com.example.workhive.controller.Register;

import com.example.workhive.domain.dto.DepartmentDTO;
import com.example.workhive.domain.dto.MemberDetailDTO;
import com.example.workhive.domain.dto.PositionDTO;
import com.example.workhive.domain.dto.TeamDTO;
import com.example.workhive.domain.entity.DepartmentEntity;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.domain.entity.PositionEntity;
import com.example.workhive.domain.entity.TeamEntity;
import com.example.workhive.repository.CompanyRepository;
import com.example.workhive.repository.MemberRepository;
import com.example.workhive.security.AuthenticatedUser;
import com.example.workhive.service.CompanyService;
import com.example.workhive.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 생성 및 참여 컨트롤러
 */
@RequiredArgsConstructor
@Controller
@RequestMapping("/register")
public class RegisterController {

    private final CompanyService companyService;
    private final CompanyRepository companyRepository;
    private final MemberRepository usersRepository;
    private final MemberService memberService;

    /**
     * 생성 참여 페이지로 이동
     * 사용자의 이름 나타내기
     *
     * @param model
     * @return
     */
    @GetMapping("roleRegister")
    public String roleregister(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
        // 로그인 한 사용자의 ID 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String memberId = auth.getName();

        String loggedInUserId = user.getMemberId();
        MemberEntity member = usersRepository.findByMemberId(loggedInUserId);

        // 사용자의 ID를 통해 사용자의 이름 가져오기
        String memberName = memberService.getMemberName(memberId);

        if (member.getRole().name().equals("ROLE_ADMIN")
                || member.getRole().name().equals("ROLE_EMPLOYEE")
                || member.getRole().name().equals("ROLE_MANAGER")) {
            return "redirect:/main/board";
        }

        model.addAttribute("memberName", memberName);
        return "register/roleRegister";
    }

    /**
     * 생성하기 버튼 클릭 시, 회사 정보 임력 폼 이동
     *
     * @param model
     * @return
     */
    @GetMapping("company")
    public String company(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
        String loggedInUserId = user.getMemberId();
        MemberEntity member = usersRepository.findById(loggedInUserId)
                .orElseThrow(() -> new RuntimeException("Member not found")); // 예외 처리

        // 회사 ID가 이미 등록되어 있는지 확인
        if (member.getCompany() != null) {
            // 이미 회사가 등록되어 있다면 adminregister로 리다이렉트
            return "redirect:/register/AdminRegister";
        }

        return "register/registerCompany";
    }

    /**
     * 회사 url 중복 체크
     *
     * @param companyUrl
     * @return
     */
    @ResponseBody
    @PostMapping("urlCheck")
    public Boolean urlCheck(@RequestParam("companyUrl") String companyUrl) {
        // 서비스의 메서드로 검색할 url을 전달받아서 조회
        boolean result = companyService.getUrl(companyUrl); // url 중복 여부 확인

        return result; // 중복되지 않으면 true 반환
    }

    /**
     * 참여하기 버튼 클릭 시 코드 입력 폼 페이지 이동
     *
     * @param model
     * @return
     */
    @GetMapping("join")
    public String join(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
        String loggedInUserId = user.getMemberId();
        MemberEntity member = usersRepository.findByMemberId(loggedInUserId);

        if (member.getRole().name().equals("ROLE_ADMIN")
                || member.getRole().name().equals("ROLE_EMPLOYEE")
                || member.getRole().name().equals("ROLE_MANAGER")) {
            return "redirect:/main/board";
        }

        return "register/registerJoin";
    }

    /**
     * 회사 코드 존재 여부 체크 후 다시 입력 or 다음 페이지
     *
     * @param code
     * @param session
     * @param model
     * @return
     */
    @PostMapping("validateInvitationCode")
    public String validateCompanyId(@RequestParam("code") String code, HttpSession session, Model model) {
        Long companyId = companyService.isValidInvitationCode(code);

        System.out.println(companyId);
        System.out.println(code);
        if (companyId != null) {
            // 유효한 경우, 세션에 companyId 저장
            session.setAttribute("companyId", companyId);
            session.setAttribute("code", code);

            return "redirect:/register/userForm"; // 다음 페이지로 리다이렉트
        } else {
            // 유효하지 않은 경우, 에러 메시지와 함께 다시 입력 페이지로
            model.addAttribute("errorMessage", "유효하지 않은 초대 코드입니다.");
            return "register/registerJoin"; // 다시 입력 페이지로 돌아감
        }
    }

    /**
     * 일반 회원 코드 등록 후 본인 정보 입력 페이지 이동
     *
     * @param model
     * @param user
     * @param session
     * @return
     */
    @GetMapping("userForm")
    public String employeeinfo(Model model, @AuthenticationPrincipal AuthenticatedUser user, HttpSession session) {
        Long companyId = (Long) session.getAttribute("companyId");

        model.addAttribute("companyId", companyId);


        System.out.println(companyId);
        String loggedInUserId = user.getMemberId();

        MemberEntity member = usersRepository.findByMemberId(loggedInUserId);
        String memberName = member.getMemberName();

        model.addAttribute("memberName", memberName);

        if (member.getRole().name().equals("ROLE_ADMIN")
                || member.getRole().name().equals("ROLE_EMPLOYEE")
                || member.getRole().name().equals("ROLE_MANAGER")) {
            return "redirect:/main/board";
        }

        model.addAttribute("loggedInUserId", loggedInUserId);


        return "register/registerUser";

    }

    /**
     * 일반회원 정보 입력 폼 제출 받아 저장
     *
     * @param memberDetailDTO
     * @param companyId
     * @param session
     * @return
     */
    @PostMapping("saveMemberDetail")
    public String saveMemberDetail(@ModelAttribute MemberDetailDTO memberDetailDTO,
                                   @RequestParam("companyId") Long companyId,
                                   HttpSession session) {
        String code = (String) session.getAttribute("code");
        System.out.println(code);
        companyService.registeremployee(memberDetailDTO, companyId, code);
        return "redirect:/main/board";
    }

    /**
     * 관리자 정보 입력하는 페이지로 이동
     * @param model
     * @param user
     * @param session
     * @return
     */
    @GetMapping("AdminRegister")
    public String adminregister(Model model, @AuthenticationPrincipal AuthenticatedUser user, HttpSession session) {

        String loggedInUserId = user.getMemberId();

        // Member 테이블에서 companyId 가져오기
        MemberEntity member = usersRepository.findByMemberId(loggedInUserId);

        Long companyId = member.getCompany().getCompanyId();
        String memberName = member.getMemberName();

        model.addAttribute("memberName", memberName);
        model.addAttribute("companyId", companyId);

        if (member.getRole().name().equals("ROLE_ADMIN")
                || member.getRole().name().equals("ROLE_EMPLOYEE")
                || member.getRole().name().equals("ROLE_MANAGER")) {
            return "redirect:/main/board";
        }

        model.addAttribute("loggedInUserId", loggedInUserId);

        return "register/registerAdmin";
    }

    /**
     * 관리자 정보 폼 제출받아 저장
     * @param memberDetailDTO
     * @param companyId
     * @param session
     * @return
     */
    @PostMapping("saveAdminDetail")
    public String saveAdminDetail(@ModelAttribute MemberDetailDTO memberDetailDTO, long companyId, HttpSession session) {

        companyService.registerAdmin(memberDetailDTO, companyId);


        return "redirect:/main/board";
    }

    /**
     * 부서 목록 조회
     * @param companyId
     * @return
     */
    @GetMapping("departments")
    @ResponseBody
    public List<DepartmentDTO> getDepartmentsByCompanyId(@RequestParam("companyId") Long companyId) {
        // 회사 URL로 부서 목록을 조회
        List<DepartmentEntity> departments = companyService.getDepartmentsByCompanyId(companyId);
        // 위에서 받아온 리스트를 스트림으로 변환
        return departments.stream()
                .map(dept -> DepartmentDTO.builder()
                        .departmentId(dept.getDepartmentId())
                        .departmentName(dept.getDepartmentName())
                        .build())
                //변환된 DepartmentDTO객체들이 리스트로 모아져 반환됨
                .collect(Collectors.toList());
    }

    /**
     * 직급 목록 조회
     * @param companyId
     * @return
     */
    @GetMapping("positions")
    @ResponseBody
    public List<PositionDTO> getPositionsCompanyId(@RequestParam("companyId") Long companyId) {
        // 회사 URL로 직급 목록을 조회
        List<PositionEntity> positions = companyService.getPositionsByCompanyId(companyId);
        // 위에서 받아온 리스트를 스트림으로 변환
        return positions.stream()
                .map(posi -> PositionDTO.builder()
                        .positionId(posi.getPositionId())
                        .positionName(posi.getPositionName())
                        .build())
                //변환된 DepartmentDTO객체들이 리스트로 모아져 반환됨
                .collect(Collectors.toList());
    }

    /**
     * 부서 번호로 하위부서(팀) 목록 조회
     * @param departmentId
     * @return
     */
    @GetMapping("teams")
    @ResponseBody
    public List<TeamDTO> getTeams(@RequestParam("departmentId") Long departmentId) {
        // 부서번호로 서브 부서 목록을 가져온다
        List<TeamEntity> Teams = companyService.getTeamsByDepartmentId(departmentId);
        System.out.println("Retrieved teams: " + Teams); // 추가된 로그
        // 위에서 받아온 리스트를 스트림으로 변환
        return Teams.stream()
                // 맵 함수를 이용해 스트림의 각 요소를 변환
                .map(subDept -> TeamDTO.builder()
                        .teamId(subDept.getTeamId())
                        .teamName(subDept.getTeamName())
                        .build())
                //변환된 TeamDTO객체들이 리스트로 모아져 반환됨
                .collect(Collectors.toList());
    }

    @PostMapping("saveCompany")
    public String saveCompany(@RequestParam Map<String, String> companyData,
                              Model model, //requestParam() 에서 ()를 넣으면 에러가 발생하는데 이유가 뭘까?
                              HttpSession session,
                              @AuthenticationPrincipal AuthenticatedUser user) {


        // companyData는 회사, 부서, 하위부서 정보를 포함한 모든 form data를 받습니다
        String loggedInUserId = user.getMemberId();
        companyData.put("memberId", loggedInUserId);
        boolean isSaved = companyService.saveCompanyAndDepartments(companyData);

        System.out.println("컴퍼니" + companyData);

        if (isSaved) {
            session.setAttribute("message", "회사와 부서 정보가 성공적으로 저장되었습니다.");
            session.setAttribute("companyId", companyData.get("companyId"));
            return "redirect:/register/AdminRegister";  // 저장 완료 후 다시 폼으로 리다이렉트
        } else {
            model.addAttribute("error", "회사와 부서 정보를 저장하는 데 문제가 발생했습니다.");

            return "register/registerCompany";
        }

    }
}
