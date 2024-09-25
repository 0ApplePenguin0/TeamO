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
}

