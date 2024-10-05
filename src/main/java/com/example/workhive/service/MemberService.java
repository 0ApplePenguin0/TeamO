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

/**
 * 회원정보 서비스
 */
@RequiredArgsConstructor
@Service
@Transactional
public class MemberService {

    //WebSecurityConfig에서 생성한 암호화 인코더
    private final BCryptPasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final MemberDetailRepository memberDetailRepository;
    private final DepartmentRepository departmentRepository;
    private final TeamRepository teamRepository;
    
 // MemberService.java
    public List<MemberDTO> getMembersByCompanyId(Long companyId) {
        List<MemberEntity> members = memberRepository.findByCompany_CompanyId(companyId);
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




    /*가입처리*/
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

        //DB에 저장
        memberRepository.save(entity);
    }

    // ID 중복 체크
    public boolean findId(String searchId) {
        return !memberRepository.existsById(searchId);
    }

    /* 이메일 중복 확인 */
    public boolean findEmail(String searchEmail) {
        return !memberRepository.existsByEmail(searchEmail);
    }

    public boolean validateUser(String searchId, String password) {
        // 아이디로 사용자 조회
        MemberEntity member = memberRepository.findById(searchId).orElse(null);

        if (member != null) {
            // 비밀번호 비교
            return passwordEncoder.matches(password, member.getMemberPassword());
        }
        return false; // 사용자 없음
    }

    public List<MemberEntity> getAllMembers() {
        return memberRepository.findAll();
    }

    public MemberDetailDTO getMemberDetailByMemberId(String memberId) {
        // members 테이블에서 유저 정보 조회
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

        // member_detail 테이블에서 상세 정보 조회
        MemberDetailEntity memberDetailEntity = memberDetailRepository.findByMember_MemberId(memberId);
        if (memberDetailEntity == null) {
            throw new IllegalArgumentException("Member detail not found with id: " + memberId);
        }

        // DTO 변환 후 리턴
        return MemberDetailDTO.builder()
                .memberDetailId(memberDetailEntity.getMemberDetailId())
                .memberId(memberDetailEntity.getMember().getMemberId()) // member 엔티티에서 memberId 추출
                .positionId(memberDetailEntity.getPosition().getPositionId()) // position 엔티티에서 positionId 추출
                .departmentId(memberDetailEntity.getDepartment().getDepartmentId()) // department 엔티티에서 departmentId 추출
                .teamId(memberDetailEntity.getTeam().getTeamId()) // team 엔티티에서 teamId 추출
                .status(memberDetailEntity.getStatus())
                .profileUrl(memberDetailEntity.getProfileUrl())
                .hireDate(memberDetailEntity.getHireDate())
                .memberName(memberEntity.getMemberName()) // member 엔티티에서 memberName 추출
                .companyId(memberEntity.getCompany().getCompanyId()) // member 엔티티에서 companyId 추출
                .build();
    }

    // 사용자 ID를 통해 사용자의 이름 찾기
    public String getMemberName(String memberId) {
        MemberEntity member = memberRepository.findByMemberId(memberId);
        return member != null ? member.getMemberName() : null;
    }

    // 부서 ID를 통해 부서명 찾기
    public String getDepartmentName(Long departmentId) {
        DepartmentEntity department = departmentRepository.findByDepartmentId(departmentId);
        return department != null ? department.getDepartmentName() : null;
    }

    public String getTeamName(Long teamId) {
        TeamEntity team = teamRepository.findByTeamId(teamId);
        return team != null ? team.getTeamName() : null;
    }

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
}


