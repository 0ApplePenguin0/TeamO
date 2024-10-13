package com.example.workhive.controller;

import com.example.workhive.domain.dto.MemberDTO;
import com.example.workhive.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberRestController {

    private final MemberService service;

    // 현재 로그인된 사용자의 memberId를 반환하는 API
    @GetMapping("/getCurrentUserMemberId")
    public String getCurrentUserMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String memberId = authentication.getName();
            log.info("Current logged in user memberId: {}", memberId);
            return memberId;
        }
        return "No user is currently logged in.";
    }

    // 현재 로그인된 사용자의 부서 ID를 반환하는 API
    @GetMapping("/getDepartmentId")
    public ResponseEntity<Long> getDepartmentId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberId = authentication.getName();

        Long departmentId = service.getDepartmentIdByMemberId(memberId);
        return ResponseEntity.ok(departmentId);
    }

    @GetMapping("/getmembers")
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        List<MemberDTO> members = service.getAllMembers();
        if (members.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(members);
    }

    @GetMapping("/getCompanyUsers")
    public List<MemberDTO> getCompanyUsers(@RequestParam Long companyId) {
        return service.getMembersByCompanyId(companyId);
    }

    @GetMapping("/getMembersByCompany/{companyId}")
    public ResponseEntity<List<MemberDTO>> getMembersByCompany(@PathVariable("companyId") Long companyId) {
        List<MemberDTO> members = service.getMembersByCompanyId(companyId);
        if (members.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(members);
    }

    @GetMapping("/getCompanyId")
    public ResponseEntity<Long> getCompanyId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberId = authentication.getName();
        Long companyId = service.getCompanyIdByMemberId(memberId);
        return ResponseEntity.ok(companyId);
    }

    // 특정 부서에 속한 멤버들을 반환하는 API
    @GetMapping("/getMembersByDepartment/{departmentId}")
    public ResponseEntity<List<MemberDTO>> getMembersByDepartment(@PathVariable("departmentId") Long departmentId) {
        List<MemberDTO> memberDTOs = service.getMembersByDepartmentId(departmentId);

        if (memberDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(memberDTOs);
    }
}
