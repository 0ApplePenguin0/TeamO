package com.example.workhive.service;


import com.example.workhive.domain.dto.MemberDTO;
import com.example.workhive.domain.dto.MemberDetailDTO;
import com.example.workhive.domain.entity.*;
import com.example.workhive.repository.*;
import com.example.workhive.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final MemberRepository usersRepository;
    private final DepartmentRepository departmentRepository;
    private final TeamRepository TeamRepository;
    private final MemberDetailRepository memberDetailRepository;
    private final PositionRepository positionRepository;
    private final InvitationCodeRepository invitationCodeRepository;
    private final MemberRepository memberRepository;

    public Long isValidInvitationCode(String code) {
        InvitationCodeEntity invitationCode = invitationCodeRepository.findByCode(code);
        System.out.println(invitationCode);
        if (invitationCode == null || !invitationCode.getIsActive()) {
            return null; // 코드가 존재하지 않거나 비활성화된 경우 null 반환
        }

        // 유효한 초대 코드가 있을 경우 company_id 반환
        System.out.println(invitationCode.getCompany().getCompanyId());
        return invitationCode.getCompany().getCompanyId();

    }

    public void registeremployee(MemberDetailDTO memberDetailDTO, Long companyId, String code) {
        DepartmentEntity departmentEntity = departmentRepository.findByDepartmentId(memberDetailDTO.getDepartmentId());
        TeamEntity TeamEntity = TeamRepository.findByTeamId(memberDetailDTO.getTeamId());
        MemberEntity memberEntity = usersRepository.findByMemberId(memberDetailDTO.getMemberId());
        PositionEntity positionEntity = positionRepository.findByPositionId(memberDetailDTO.getPositionId());
        CompanyEntity companyEntity = companyRepository.findByCompanyId(companyId);

        memberEntity.setRole(MemberEntity.RoleEnum.valueOf("ROLE_EMPLOYEE"));
        memberEntity.setCompany(companyEntity);
        usersRepository.save(memberEntity);

        AuthenticatedUser currentUser = (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // AuthenticatedUser의 권한 변경
        currentUser.setRole(MemberEntity.RoleEnum.valueOf("ROLE_EMPLOYEE")); // role 필드의 setter 사용

        // 새로운 권한 정보를 SecurityContext에 반영
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                currentUser,
                currentUser.getPassword(),
                currentUser.getAuthorities() // 새로운 권한으로 다시 설정
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        MemberDetailEntity memberDetailEntity= new MemberDetailEntity();
        memberDetailEntity.setMember(memberEntity);
        memberDetailEntity.setDepartment(departmentEntity);
        memberDetailEntity.setTeam(TeamEntity);
        memberDetailEntity.setHireDate(memberDetailDTO.getHireDate());
        memberDetailEntity.setPosition(positionEntity);

        memberDetailEntity.setStatus("재직");
        memberDetailEntity.setProfileUrl("icon");

        memberDetailRepository.save(memberDetailEntity);

        InvitationCodeEntity invitationCodeEntity = invitationCodeRepository.findByCode(code);

        invitationCodeEntity.setUsageLimit(invitationCodeEntity.getUsageLimit() - 1);
        invitationCodeEntity.setUsageCount(invitationCodeEntity.getUsageCount() + 1);
        invitationCodeEntity.setIsActive(false); // is_active를 false로 설정
        invitationCodeRepository.save(invitationCodeEntity);
    }

    public void registerAdmin(MemberDetailDTO memberDetailDTO, Long companyId) {
        MemberEntity memberEntity = usersRepository.findByMemberId(memberDetailDTO.getMemberId());

        CompanyEntity companyEntity = companyRepository.findByCompanyId(companyId);
        DepartmentEntity departmentEntity = departmentRepository.findByDepartmentId(memberDetailDTO.getDepartmentId());
        TeamEntity teamEntity = TeamRepository.findByTeamId(memberDetailDTO.getTeamId());
        PositionEntity positionEntity = positionRepository.findByPositionId(memberDetailDTO.getPositionId());


        memberEntity.setRole(MemberEntity.RoleEnum.valueOf("ROLE_ADMIN"));
        memberEntity.setCompany(companyEntity);
        usersRepository.save(memberEntity);

        AuthenticatedUser currentUser = (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // AuthenticatedUser의 권한 변경
        currentUser.setRole(MemberEntity.RoleEnum.valueOf("ROLE_ADMIN")); // role 필드의 setter 사용

        // 새로운 권한 정보를 SecurityContext에 반영
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                currentUser,
                currentUser.getPassword(),
                currentUser.getAuthorities() // 새로운 권한으로 다시 설정
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);


        MemberDetailEntity memberDetailEntity= new MemberDetailEntity();
        memberDetailEntity.setMember(memberEntity);
        memberDetailEntity.setDepartment(departmentEntity);
        memberDetailEntity.setTeam(teamEntity);
        memberDetailEntity.setHireDate(memberDetailDTO.getHireDate());
        memberDetailEntity.setPosition(positionEntity);

        memberDetailEntity.setStatus("재직");
        memberDetailEntity.setProfileUrl("icon");

        memberDetailRepository.save(memberDetailEntity);
    }


    @Transactional
    public boolean saveCompanyAndDepartments(Map<String, String> companyData) {
        try {
            String memberId = companyData.get("memberId");

            MemberEntity member = usersRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("Member not found"));
            System.out.println("Received company data: " + companyData);
            CompanyEntity company = new CompanyEntity();
            company.setCompanyName(companyData.get("company_name"));
            String companyAddress = companyData.get("company_address");
            company.setCompanyAddress(companyAddress); // 통합된 주소
            company.setCompanyUrl(companyData.get("company_url"));
            System.out.println(company);
            companyRepository.save(company);

            member.setCompany(company); // 회원에 회사 설정
            usersRepository.save(member); // 업데이트된 멤버 저장

            companyData.put("companyId", company.getCompanyId().toString());

            // 부서 정보 저장
            int departmentCount = 1;
            while (companyData.containsKey("department[" + departmentCount + "][name]")) {
                String departmentName = companyData.get("department[" + departmentCount + "][name]");
                DepartmentEntity department = new DepartmentEntity();
                department.setCompany(company);
                department.setDepartmentName(departmentName);
                System.out.println(departmentName);
                departmentRepository.save(department);

                // 하위부서 정보 저장
                int teamCount = 1;
                while (companyData.containsKey("department[" + departmentCount + "][teams][" + teamCount + "][name]")) {
                    String teamName = companyData.get("department[" + departmentCount + "][teams][" + teamCount + "][name]");
                    TeamEntity team = new TeamEntity(); // TeamEntity 객체 생성
                    team.setDepartment(department); // 해당 부서와 연결
                    team.setTeamName(teamName); // 팀 이름 설정
                    TeamRepository.save(team); // 팀 정보 DB에 저장
                    System.out.println(team);
                    teamCount++; // 팀 카운터 증가
                }
                departmentCount++;
            }

            int positionCount = 1;
            while (companyData.containsKey("positions[" + positionCount + "][name]")) {
                String positionName = companyData.get("positions[" + positionCount + "][name]");
                PositionEntity position = new PositionEntity();
                position.setPositionName(positionName);
                position.setCompany(company);
                positionRepository.save(position);
                positionCount++;
            }



            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<PositionEntity> getPositionsByCompanyId(Long companyId) {
        // 회사 URL로 직급 목록 조회
        return positionRepository.findByCompany_CompanyId(companyId);
    }

}