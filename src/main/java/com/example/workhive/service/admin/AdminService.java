package com.example.workhive.service.admin;


import com.example.workhive.domain.dto.MemberDetailDTO;
import com.example.workhive.domain.entity.*;
import com.example.workhive.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final CompanyRepository companyRepository;
    private final MemberRepository usersRepository;
    private final DepartmentRepository departmentRepository;
    private final MemberDetailRepository memberDetailRepository;
    private final PositionRepository positionRepository;
    private final TeamRepository teamRepository;

    // 직원 목록 조회 메서드 (검색어 적용)
            public List<Map<String, String>> getMembersByCompanyId(Long companyId, String searchType, String searchWord, boolean searchEnabled) {
                List<MemberEntity> members;

                // 검색어에 따라 조건 적용
                if (searchType.equals("name") && !searchWord.isEmpty() && !searchEnabled) {
                    members = usersRepository.findByCompany_CompanyIdAndMemberNameContaining(companyId, searchWord);
                } else {
                    members = usersRepository.findByCompany_CompanyId(companyId);
                }


                // 각 직원 정보를 Map으로 변환하여 반환
                return members.stream().map(member -> {
                    MemberDetailEntity detail = memberDetailRepository.findByMember_MemberId(member.getMemberId());

                    // 각 ID로 이름 조회
                    String positionName = positionRepository.findById(detail.getPosition().getPositionId())
                            .map(PositionEntity::getPositionName)
                            .orElse("N/A");

                    String departmentName = departmentRepository.findById(detail.getDepartment().getDepartmentId())
                            .map(DepartmentEntity::getDepartmentName)
                            .orElse("N/A");

                    String teamName = teamRepository.findById(detail.getTeam().getTeamId())
                            .map(TeamEntity::getTeamName)
                            .orElse("N/A");

                    Map<String, String> memberInfo = new HashMap<>();
                    memberInfo.put("memberId", member.getMemberId());
                    memberInfo.put("memberName", member.getMemberName());
                    memberInfo.put("positionName", positionName);
                    memberInfo.put("departmentName", departmentName);
                    memberInfo.put("teamName", teamName);
                    memberInfo.put("role", member.getRole().name());

                    return memberInfo; // Map 반환
                }).collect(Collectors.toList());
            }

    public void updateMember(MemberDetailDTO memberDetailDTO, String roleString) {
        // 1. 멤버 디테일 정보 업데이트
        MemberEntity memberEntity = usersRepository.findByMemberId(memberDetailDTO.getMemberId());
        MemberDetailEntity memberDetailEntity = memberDetailRepository.findByMember_MemberId(memberDetailDTO.getMemberId());
        DepartmentEntity departmentEntity = departmentRepository.findByDepartmentId(memberDetailDTO.getDepartmentId());
        TeamEntity teamEntity = teamRepository.findByTeamId(memberDetailDTO.getTeamId());
        PositionEntity positionEntity = positionRepository.findByPositionId(memberDetailDTO.getPositionId());


        memberDetailEntity.setDepartment(departmentEntity);
        memberDetailEntity.setTeam(teamEntity);
        memberDetailEntity.setPosition(positionEntity);
        memberDetailEntity.setStatus(memberDetailDTO.getStatus());
        memberDetailRepository.save(memberDetailEntity); // 멤버 디테일 저장

        MemberEntity.RoleEnum role = MemberEntity.RoleEnum.valueOf(roleString);
        memberEntity.setRole(role);
        usersRepository.save(memberEntity); // 멤버 정보 저장
    }

    public List<TeamEntity> getTeamsByDepartmentId(Long departmentId) {
        return teamRepository.findByDepartmentDepartmentId(departmentId);
    }
}
