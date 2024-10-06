package com.example.workhive.controller.Approval;

import com.example.workhive.domain.dto.MemberDTO;
import com.example.workhive.service.approval.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamApiController {

    private final ApprovalService approvalService;

    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<MemberDTO>> getMembersByTeam(@PathVariable Long teamId) {
        List<MemberDTO> members = approvalService.getMembersByTeam(teamId);
        return ResponseEntity.ok(members);
    }
}