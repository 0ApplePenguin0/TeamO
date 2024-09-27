package com.example.workhive.controller;

import com.example.workhive.domain.dto.MemberDetailDTO;
import com.example.workhive.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.naming.Name;
import java.util.Enumeration;

@Slf4j
@Controller
@RequestMapping("main")
@RequiredArgsConstructor
public class MainController {

    private final MemberService memberService;

    @GetMapping("board")
    public String board(HttpSession session) {
        // 현재 로그인한 유저의 ID를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberId = authentication.getName();

        // memberId를 이용해 유저의 상세 정보를 조회
        MemberDetailDTO memberDetail = memberService.getMemberDetailByMemberId(memberId);

        // 세션에 값 저장 (이미 존재하는 값이 있을 경우 덮어쓰기)
        session.setAttribute("memberId", memberDetail.getMemberId());
        session.setAttribute("companyId", memberDetail.getCompanyId());
        session.setAttribute("positionId", memberDetail.getPositionId());
        session.setAttribute("departmentId", memberDetail.getDepartmentId());
        session.setAttribute("teamId", memberDetail.getTeamId());
        session.setAttribute("memberName", memberDetail.getMemberName());

        // 세션의 모든 값 출력
        printSessionAttributes(session);

        return "main/mainboard";
    }

    @GetMapping("roleregister")
    public String roleregister() {
        return "main/roleregister";
    }

    // 세션 값 출력 메서드
    private void printSessionAttributes(HttpSession session) {
        Enumeration<String> attributeNames = session.getAttributeNames();

        // 모든 세션 값을 로그로 출력
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            Object attributeValue = session.getAttribute(attributeName);

            log.debug("세션에 들어있는 값: {} = {}", attributeName, attributeValue);  // 로그로 출력
        }
    }
}
