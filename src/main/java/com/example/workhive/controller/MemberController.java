package com.example.workhive.controller;

import com.example.workhive.domain.dto.MemberDTO;
import com.example.workhive.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("joinForm")
    public String join() {
        return "member/join";
    }


    // 회원가입 처리
    @PostMapping("join")
    public String join(@ModelAttribute MemberDTO member) {
        log.debug("전달된 회원정보 : {}", member);
        service.join(member);
        return "redirect:/";
    }

    //ID 중복 확인 모달
    @ResponseBody
    @PostMapping("idCheck")
    public Boolean idCheck(Model model, @RequestParam("searchId") String searchId) {

        //서비스의 메소드로 검색할 아이디를 전달받아서 조회
        boolean result = service.findId(searchId);

        //검색한 아이디와 조회결과를 모델에 저장
        model.addAttribute("searchId", searchId);
        model.addAttribute("result", result);
        //검색 페이지로 다시 이동
        return result;
    }

    @ResponseBody
    @PostMapping("emailCheck")
    public Boolean emailCheck(@RequestParam("searchEmail") String searchEmail) {
        // 이메일 중복 확인을 위한 로그 출력
        log.debug("검색할 이메일: {}", searchEmail);

        // 서비스의 메서드로 검색할 이메일을 전달받아서 조회
        boolean result = service.findEmail(searchEmail); // 이메일 중복 여부 확인

        // 결과를 반환 (중복이면 false, 사용 가능하면 true)
        return result; // 중복되지 않으면 true 반환
    }


    // 로그인 양식으로 이동
    @GetMapping("loginForm")
    public String login() {
        return "member/login";
    }

    //로그인 처리
    @PostMapping("login")
    public String login(@ModelAttribute MemberDTO member) {
        if (service.validateUser(member.getMemberId(), member.getMemberPassword())) {
            // 로그인 성공 로직
            return "redirect:/";
        } else {
            // 로그인 실패 로직
            return "member/loginForm?error=true";
        }
    }
}
