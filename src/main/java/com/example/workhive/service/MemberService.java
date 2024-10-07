package com.example.workhive.service;

import com.example.workhive.domain.dto.MemberDTO;
import com.example.workhive.domain.dto.MemberDetailDTO;
import com.example.workhive.domain.entity.*;
import com.example.workhive.repository.DepartmentRepository;
import com.example.workhive.repository.MemberDetailRepository;
import com.example.workhive.repository.MemberRepository;
import com.example.workhive.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final MemberDetailRepository memberDetailRepository;
    private final DepartmentRepository departmentRepository;
    private final TeamRepository teamRepository;

    // 회사 ID를 기준으로 멤버를 조회하고 DTO로 변환하는 메서드
    public List<MemberDTO> getMembersByCompanyId(Long companyId) {
        List<MemberEntity> members = memberRepository.findByCompany_CompanyId(companyId);
        return convertToDTOList(members);  // DTO 변환
    }

    // 부서 ID로 멤버 조회하고 DTO로 변환하는 메서드
    public List<MemberDTO> getMembersByDepartmentId(Long departmentId) {
        List<MemberEntity> members = memberRepository.findByMemberDetail_Department_DepartmentId(departmentId);
        return convertToDTOList(members);  // DTO 변환
    }

    // 멤버 ID로 부서 ID를 조회하는 메서드
    public Long getDepartmentIdByMemberId(String memberId) {
        MemberDetailEntity memberDetail = memberDetailRepository.findByMember_MemberId(memberId);
        if (memberDetail == null || memberDetail.getDepartment() == null) {
            throw new IllegalArgumentException("Department not found for Member ID: " + memberId);
        }
        return memberDetail.getDepartment().getDepartmentId();
    }

    // 모든 멤버 조회 시 DTO 변환
    public List<MemberDTO> getAllMembers() {
        List<MemberEntity> members = memberRepository.findAll();
        return convertToDTOList(members);  // DTO 변환
    }

    // Entity 리스트를 DTO 리스트로 변환하는 메서드
    private List<MemberDTO> convertToDTOList(List<MemberEntity> members) {
        List<MemberDTO> memberDTOs = new ArrayList<>();
        
        for (MemberEntity member : members) {
            MemberDTO memberDTO = MemberDTO.builder()
                    .memberId(member.getMemberId())
                    .memberName(member.getMemberName())
                    .email(member.getEmail())
                    .build();
            memberDTOs.add(memberDTO);
        }
        
        return memberDTOs;
    }

    // 회원 가입 처리
    public void join(MemberDTO dto) {

        MemberEntity entity = new MemberEntity();
        entity.setMemberId(dto.getMemberId());
        entity.setMemberName(dto.getMemberName());
        entity.setMemberPassword(passwordEncoder.encode(dto.getMemberPassword()));

        String email = dto.getEmail();
        if (email != null && email.trim().isEmpty()) {
            email = null;
        }
        entity.setEmail(email);

        memberRepository.save(entity);
    }

    // ID 중복 체크
    public boolean findId(String searchId) {
        return !memberRepository.existsById(searchId);
    }

    // 이메일 중복 확인
    public boolean findEmail(String searchEmail) {
        return !memberRepository.existsByEmail(searchEmail);
    }

    // 사용자 인증 처리
    public boolean validateUser(String searchId, String password) {
        MemberEntity member = memberRepository.findById(searchId).orElse(null);

        if (member != null) {
            return passwordEncoder.matches(password, member.getMemberPassword());
        }
        return false; // 사용자 없음
    }

    // 사용자 상세 정보 조회
    public MemberDetailDTO getMemberDetailByMemberId(String memberId) {
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

        MemberDetailEntity memberDetailEntity = memberDetailRepository.findByMember_MemberId(memberId);
        if (memberDetailEntity == null) {
            throw new IllegalArgumentException("Member detail not found with id: " + memberId);
        }

        return MemberDetailDTO.builder()
                .memberDetailId(memberDetailEntity.getMemberDetailId())
                .memberId(memberDetailEntity.getMember().getMemberId())
                .positionId(memberDetailEntity.getPosition().getPositionId())
                .departmentId(memberDetailEntity.getDepartment().getDepartmentId())
                .teamId(memberDetailEntity.getTeam().getTeamId())
                .status(memberDetailEntity.getStatus())
                .profileUrl(memberDetailEntity.getProfileUrl())
                .hireDate(memberDetailEntity.getHireDate())
                .memberName(memberEntity.getMemberName())
                .companyId(memberEntity.getCompany().getCompanyId())
                .build();
    }

    // 사용자 이름 조회
    public String getMemberName(String memberId) {
        MemberEntity member = memberRepository.findByMemberId(memberId);
        return member != null ? member.getMemberName() : null;
    }

    // 부서명 조회
    public String getDepartmentName(Long departmentId) {
        DepartmentEntity department = departmentRepository.findByDepartmentId(departmentId);
        return department != null ? department.getDepartmentName() : null;
    }

    // 팀명 조회
    public String getTeamName(Long teamId) {
        TeamEntity team = teamRepository.findByTeamId(teamId);
        return team != null ? team.getTeamName() : null;
    }

    // 사용자 이메일 조회
    public String getEmail(String memberId) {
        MemberEntity member = memberRepository.findById(memberId).orElse(null);
        return member != null ? member.getEmail() : null;
    }

    public CompanyEntity getCompanyById(String memberId) {
        MemberEntity member = memberRepository.findById(memberId).orElse(null);
        return member != null ? member.getCompany() : null;
    }

    /**
     * memberId를 통해 companyId 가져오기
     * @param memberId
     * @return
     */
    public Long getCompanyIdByMemberId(String memberId) {
        return memberRepository.findById(memberId)
                .map(member -> member.getCompany().getCompanyId())
                .orElseThrow(() -> new RuntimeException("Member not found"));
    }

    // 멤버 ID로 회사 ID 조회
    public Long getCompanyIdByMemberId(String memberId) {
        MemberEntity member = memberRepository.findByMemberId(memberId);
        if (member == null || member.getCompany() == null) {
            throw new IllegalArgumentException("Member or company details not found for ID: " + memberId);
        }
        return member.getCompany().getCompanyId();
    }
}
