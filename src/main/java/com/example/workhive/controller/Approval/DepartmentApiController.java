package com.example.workhive.controller.Approval;

import com.example.workhive.domain.dto.TeamDTO;
import com.example.workhive.service.approval.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentApiController {

    private final ApprovalService approvalService;

    @GetMapping("/{departmentId}/teams")
    public ResponseEntity<List<TeamDTO>> getTeamsByDepartment(@PathVariable Long departmentId) {
        List<TeamDTO> teams = approvalService.getTeamsByDepartment(departmentId);
        return ResponseEntity.ok(teams);
    }
}