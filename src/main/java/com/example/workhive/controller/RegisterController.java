package com.example.workhive.controller;

import com.example.workhive.domain.dto.MemberDTO;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.repository.MemberRepository;
import com.example.workhive.security.AuthenticatedUser;
import com.example.workhive.service.CompanyService;
import com.example.workhive.service.MemberService;
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
    public String company(Model model){
        return "register/createCompany";
    }

    @ResponseBody
    @PostMapping("urlCheck")
    public Boolean emailCheck(@RequestParam("searchUrl") String searchEmail) {
        // 서비스의 메서드로 검색할 url을 전달받아서 조회
        boolean result = companyService.; // url 중복 여부 확인

        // 결과를 반환 (중복이면 false, 사용 가능하면 true)
        return result; // 중복되지 않으면 true 반환
    }

}
