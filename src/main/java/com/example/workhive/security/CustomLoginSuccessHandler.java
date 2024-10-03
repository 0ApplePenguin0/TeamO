package com.example.workhive.security;

import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 로그인한 사용자 정보 가져오기
        String memberId = authentication.getName();
        MemberEntity member = memberRepository.findByMemberId(memberId);

        // null 체크
        if (member == null) {
            // Handle the case where member is not found
            response.sendRedirect("/login?error=UserNotFound");
            return;
        }

        // rolename에 따라 리다이렉트
        MemberEntity.RoleEnum rolename = member.getRole();
        if (rolename == MemberEntity.RoleEnum.ROLE_USER) {
            // user는 생성하기, 참가하기만 있는 메인 페이지로 이동
            response.sendRedirect("/register/roleRegister");
        } else if (rolename == MemberEntity.RoleEnum.ROLE_ADMIN) {
            // admin은 해당 회사의 메인 페이지로 이동
            response.sendRedirect("/main/board");
        } else if (rolename == MemberEntity.RoleEnum.ROLE_EMPLOYEE) {
            // employee는 해당 회사의 메인 페이지로 이동
            response.sendRedirect("/main/board");
        } else {
            // 그 외의 경우 홈으로 리다이렉트
            response.sendRedirect("/");
        }
    }
}