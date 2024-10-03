package com.example.workhive.controller;

import com.example.workhive.domain.entity.MemberEntity;
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

/**
 * 생성 및 참여 컨트롤러
 */
@RequiredArgsConstructor
@Controller
@RequestMapping("/register")
public class RegisterController {

    private final CompanyService companyService;
    private final MemberRepository usersRepository;
    private final MemberService memberService;

    /**
     * 생성 참여 페이지로 이동
     * 사용자의 이름 나타내기
     * @param model
     * @return
     */
    @GetMapping("roleRegister")
    public String roleregister(Model model , @AuthenticationPrincipal AuthenticatedUser user){
        // 로그인 한 사용자의 ID 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String memberId  = auth.getName();

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
        return "register/roleRegister"; }

    /**
     * 생성하기 버튼 클릭 시, 회사 정보 임력 폼 이동
     * @param model
     * @return
     */
    @GetMapping("company")
    public String company(Model model, @AuthenticationPrincipal AuthenticatedUser user){
        String loggedInUserId = user.getMemberId();
        MemberEntity member = usersRepository.findById(loggedInUserId)
                .orElseThrow(() -> new RuntimeException("Member not found")); // 예외 처리

        // 회사 ID가 이미 등록되어 있는지 확인
        if (member.getCompany() != null) {
            // 이미 회사가 등록되어 있다면 adminregister로 리다이렉트
            return "redirect:/main/company/AdminRegister";
        }

        return "register/registerCompany";
    }

    /**
     * 회사 url 중복 체크
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
     * @param model
     * @return
     */
    @GetMapping("join")
    public String join(Model model, @AuthenticationPrincipal AuthenticatedUser user){
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

            return "redirect:/main/company/EmployeeInfo"; // 다음 페이지로 리다이렉트
        } else {
            // 유효하지 않은 경우, 에러 메시지와 함께 다시 입력 페이지로
            model.addAttribute("errorMessage", "유효하지 않은 초대 코드입니다.");
            return "register/registerJoin"; // 다시 입력 페이지로 돌아감
        }
    }

}
