package com.example.workhive.controller;

import com.example.workhive.domain.dto.MemberDTO;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.repository.MemberRepository;
import com.example.workhive.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberRestController {
	
	private final MemberService service;
	private final MemberRepository memberRepository;

    // 현재 로그인된 사용자의 memberId를 반환하는 API
    @GetMapping("/getCurrentUserMemberId")
    public String getCurrentUserMemberId() {
        // Spring Security를 사용하여 현재 인증된 사용자의 정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String memberId = authentication.getName(); // 로그인한 사용자의 ID를 가져옴
            log.info("Current logged in user memberId: {}", memberId);
            return memberId;
        }
        return "No user is currently logged in.";
    }
    
    @GetMapping("/getmembers")
    public List<MemberDTO> getAllMembers() {
        List<MemberEntity> members = service.getAllMembers();
        List<MemberDTO> memberDTOs = new ArrayList<>();

        // MemberEntity 리스트를 MemberDTO 리스트로 변환
        for (MemberEntity member : members) {
            MemberDTO memberDTO = MemberDTO.builder()
                    .memberId(member.getMemberId())
                    .email(member.getEmail())
                    .memberName(member.getMemberName())
//                    .memberPassword(member.getMemberPassword())
//                    .role(member.getRole())
                    .companyId(member.getCompany() != null ? member.getCompany().getCompanyId() : null)
                    .build();
            memberDTOs.add(memberDTO);
        }

        return memberDTOs;
    }
    
    @GetMapping("/getCompanyId")
    public ResponseEntity<Long> getCompanyId() {
        // 현재 로그인된 사용자의 정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        String memberId = authentication.getName(); // 로그인된 사용자 정보는 항상 있다고 가정
        MemberEntity member = memberRepository.findByMemberId(memberId);
        log.debug("멤버의 아이디 : ", member.getMemberId());
        
        // 사용자의 회사 정보 가져오기
        Long companyId = member.getCompany().getCompanyId();
        return ResponseEntity.ok(companyId);
    }
}

