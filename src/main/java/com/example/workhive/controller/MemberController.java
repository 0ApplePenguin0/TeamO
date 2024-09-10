package com.example.workhive.controller;

import com.example.workhive.domain.dto.MemberDTO;
import com.example.workhive.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 회원 관련 콘트롤러
 */

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("member")
public class MemberController {


    private final MemberService service;

    /*
     * 회원가입양식으로 이동
     * */
    @GetMapping("joinForm")//외부로 보이는 주소
    public String join() {
        // ..."C:/java/workspace/a.html"

        return "member/joinForm"; //내부의 주소(상대경로)
    }

    @PostMapping("joinForm")
    public String join(@ModelAttribute MemberDTO member) {
        log.debug("전달된 회원정보 : {}", member);
        //서비스로 전달하여 저장 (메서드 추가)
        service.join(member);
        return "redirect:/";
    }

    @GetMapping("loginForm")
    public String login() {
        return "member/loginForm";
    }

    @PostMapping("login")
    public String login(@ModelAttribute MemberDTO member) {
        return "redirect:/";
    }

    @GetMapping("logout")
    public String logout() {
        return "member/logout";
    }
}
