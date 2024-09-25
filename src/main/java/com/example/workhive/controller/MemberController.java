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
    @GetMapping("joinForm")//외부로 보이는 주소
    public String join() {
        // ..."C:/java/workspace/a.html"

        return "member/join"; //내부의 주소(상대경로)
    }

    @PostMapping("join")
    public String join(@ModelAttribute MemberDTO member) {
        log.debug("전달된 회원정보 : {}", member);
        //서비스로 전달하여 저장 (메서드 추가)
        service.join(member);
        return "redirect:/";
    }

    @ResponseBody
    @PostMapping("idCheck")
    public Boolean idCheck(Model model, @RequestParam("searchId") String searchId) {
        //ID중복확인 폼에서 전달된 검색할 아이디를 받아서 log출력
        log.debug("검색할 아이디: {}", searchId);
        //서비스의 메소드로 검색할 아이디를 전달받아서 조회
        boolean result = service.findId(searchId);
        // 해당 아이디를 쓰는 회원이 있으면 false, 없으면 true 리턴받음
        //=>서비스쪽에 구현

        //검색한 아이디와 조회결과를 모델에 저장
        model.addAttribute("searchId", searchId);
        model.addAttribute("result", result);
        //검색 페이지로 다시 이동
        return result;
    }

    @GetMapping("emailCheck")
    public String emailCheck() {
        return "member/emailCheck"; // 이메일 중복 확인 페이지로 이동
    }

    @PostMapping("emailCheck")
    public String emailCheck(Model model, @RequestParam("searchEmail") String searchEmail) {
// 이메일 중복 확인 폼에서 전달된 검색할 이메일을 받아서 log 출력
        log.debug("검색할 이메일: {}", searchEmail);

// 서비스의 메소드로 검색할 이메일을 전달받아서 조회
        boolean result = service.findEmail(searchEmail);
// 해당 이메일을 쓰는 회원이 있으면 false, 없으면 true 리턴받음
// => 서비스쪽에 구현

// 검색한 이메일과 조회 결과를 모델에 저장
        model.addAttribute("searchEmail", searchEmail);
        model.addAttribute("result", result);

// 검색 페이지로 다시 이동
        return "member/emailCheck"; // 이메일 중복 확인 페이지로 이동
    }


    @GetMapping("loginForm")
    public String login() {
        return "member/login";
    }

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

    @GetMapping("logout")
    public String logout() {
        return "/";
    }
}
