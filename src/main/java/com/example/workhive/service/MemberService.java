package com.example.workhive.service;

import com.example.workhive.domain.dto.MemberDTO;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    }


    //get all members
    public List<MemberEntity> getAllMembers() {
        return memberRepository.findAll();
    }
}

