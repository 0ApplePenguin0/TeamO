package com.example.workhive.controller;

import com.example.workhive.domain.dto.MemberDTO;
import com.example.workhive.security.AuthenticatedUser;
import com.example.workhive.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 생성 및 참여 컨트롤러
 */
@RequiredArgsConstructor
@Controller
@RequestMapping("/register")
public class RegisterController {

    private final CompanyService companyService;

    /**
     * 생성 참여 페이지로 이동
     * 사용자의 이름 나타내기
     * @param model
     * @return
     */
    @GetMapping("roleRegister")
    public String roleregister(Model model){
        // 로그인 한 사용자의 ID 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String memberId  = auth.getName();
        
        model.addAttribute("memberName", memberId);
        return "register/roleRegister"; }

    @GetMapping("company")
    public String company(Model model){
        return "main/company/CompanyRegister";
    }

}
