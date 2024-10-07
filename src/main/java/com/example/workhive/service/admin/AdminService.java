package com.example.workhive.service.admin;


import com.example.workhive.domain.dto.MemberDetailDTO;
import com.example.workhive.domain.entity.*;
import com.example.workhive.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminService {

    private final CompanyRepository companyRepository;
    private final MemberRepository usersRepository;
    private final DepartmentRepository departmentRepository;
    private final MemberDetailRepository memberDetailRepository;
    private final PositionRepository positionRepository;
    private final TeamRepository teamRepository;
    private final InvitationCodeRepository invitationCodeRepository;

    // 직원 목록 조회 메서드 (검색어 적용)
            public List<Map<String, String>> getMembersByCompanyId(Long companyId, String searchType, String searchWord, boolean searchEnabled) {
                List<MemberEntity> members;

                // 검색어에 따라 조건 적용
                if (searchType.equals("name") && !searchWord.isEmpty()) {
                    // 검색 조건
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

    @Transactional
    public boolean updateCompanyWithDepartments(Map<String, String> companyData) {
        try {
            // 회사 ID 등을 기반으로 기존 회사 찾기
            String memberId = companyData.get("memberId");
            MemberEntity member = usersRepository.findByMemberId(memberId);
            Long companyId = member.getCompany().getCompanyId();
            CompanyEntity company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            boolean isDepartmentAdded = false;
            // 부서와 팀 추가
            for (Map.Entry<String, String> entry : companyData.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();


                System.out.println("현재 키: " + key);

                // 팀 추가 조건
                if (key.startsWith("departments[") && key.contains("][teams][")) {

                    DepartmentEntity department = null;

                    if (!isDepartmentAdded) {
                        //DepartmentId값
                        Long departmentId = (long) Integer.parseInt(key.substring(12, key.indexOf("][teams][")));

                        department = departmentRepository.findByDepartmentId(departmentId);


                    } else {
                        //index값
                        int departmentIndex = Integer.parseInt(key.substring(12, key.indexOf("][teams]["))) + 1;
                        List<DepartmentEntity> allDepartments = departmentRepository.findAll();
                        department = allDepartments.get(departmentIndex);
                    }



                    // 기존 부서 찾기 (부서 ID로)


                        String teamName = value; // 팀 이름 가져오기
                        if (teamName != null && !teamName.trim().isEmpty()) {
                            TeamEntity team = new TeamEntity();
                            team.setTeamName(teamName);
                            team.setDepartment(department); // 부서와 팀 연결
                            System.out.println("팀 추가: " + teamName);
                            teamRepository.save(team);
                        } else {
                            System.out.println("팀 이름이 비어있거나 null입니다: " + key);
                        }
                    } else {
                        System.out.println("기존 부서 없음");
                    }


                // 부서 추가 조건
                if (key.startsWith("departments[") && key.endsWith("][name]") && !key.contains("teams")) {
                    // 부서 ID 추출 (부서 관련 키에서)
                    int departmentIndex = Integer.parseInt(key.substring(12, key.indexOf("][name]")));
                    System.out.println("부서 인덱스: " + departmentIndex);
                    isDepartmentAdded = true;

                    // 기존 부서 찾기 (부서 ID로)
                    DepartmentEntity department = departmentRepository.findByDepartmentId((long) departmentIndex);

                    // 부서 추가
                    if (department == null) {
                        department = new DepartmentEntity();
                        department.setDepartmentName(value);
                        department.setCompany(member.getCompany());
                        System.out.println("새 부서 생성: " + value);
                        departmentRepository.saveAndFlush(department);
                    } else {
                        System.out.println("기존 부서 사용: " + department.getDepartmentName());
                    }
                }
            }


            // 직급 추가
            for (Map.Entry<String, String> entry : companyData.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("positions[") && key.endsWith("][name]")) {
                    String positionName = entry.getValue();
                    if (positionName != null && !positionName.trim().isEmpty()) {
                        PositionEntity position = new PositionEntity();
                        position.setPositionName(positionName);
                        position.setCompany(member.getCompany());
                        System.out.println("직급 확인용: " + positionName);
                        positionRepository.save(position);
                    }
                }
            }

            return true;
        } catch (Exception e) {
            System.out.println("에러 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void deleteTeam(Long teamId) {
        TeamEntity team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("해당 팀은 없습니다."));
       teamRepository.delete(team);
    }

    public String generateInvitationCode(CompanyEntity company, MemberEntity createdBy, LocalDateTime expirationDate) {
        if (company == null || createdBy == null) {
            throw new IllegalArgumentException("Company or CreatedBy entity cannot be null.");
        }
        String code = UUID.randomUUID().toString();
        InvitationCodeEntity invitationCode = InvitationCodeEntity.builder()
                .code(code)
                .company(company)
                .isActive(true)
                .usageLimit(1)
                .usageCount(0)  // 기본값을 명시적으로 설정
                .expirationDate(expirationDate)
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())  // 생성 시간 설정
                .build();
        
        invitationCodeRepository.save(invitationCode);
        System.out.println("결과 확인" + invitationCode);
        return code;
    }

    //초대 코드 유효성 검사
    public Long validateInvitationCode(String code) {
        InvitationCodeEntity invitationCode = invitationCodeRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대 코드입니다."));

        if (invitationCode.getExpirationDate() != null && invitationCode.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("초대 코드가 만료되었습니다.");
        }

        if (invitationCode.getUsageLimit() != null && invitationCode.getUsageCount() >= invitationCode.getUsageLimit()) {
            throw new IllegalArgumentException("초대 코드의 사용 횟수 제한에 도달했습니다.");
        }

        if (invitationCode.getIsActive()) {
            throw new IllegalArgumentException("유효한 코드입니다.");
        }

        return invitationCode.getCompany().getCompanyId();
    }
}
