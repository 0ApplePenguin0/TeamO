package com.example.workhive.service;

import com.example.workhive.domain.dto.MemberDTO;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<MemberEntity> getAllMembers() {
        return memberRepository.findAll();
    }

    /*가입처리*/
    public void join(MemberDTO dto) {
    	
    	  if (dto.getMemberPassword() == null || dto.getMemberPassword().isEmpty()) {
    	        throw new IllegalArgumentException("비밀번호가 비어있습니다.");
    	    }
        MemberEntity entity = new MemberEntity();
        entity.setMemberId(dto.getMemberId());
        entity.setMemberName(dto.getMemberName());
        entity.setMemberPassword(passwordEncoder.encode(dto.getMemberPassword()));
        entity.setEmail(dto.getEmail());
        entity.setRole(MemberEntity.Role.ROLE_EMPLOYEE); // 열거형으로 설정(Eum)

        // DB에 저장
        memberRepository.save(entity);
    }


    /* 아이디 중복 확인 */
    public boolean findId(String searchId) {
       return !memberRepository.existsById(searchId);
    }

    
    /**
     * 전달받은 아이디와 비밀번호를 사용하여 DB에서 사용자 정보를 조회합니다.
     *
     * @param searchId 조회할 아이디
     * @param password 입력받은 비밀번호
     * @return 사용자가 존재하고 비밀번호가 일치하면 true, 그렇지 않으면 false
     */
    public boolean validateUser(String searchId, String password) {
        // 아이디로 사용자 조회
        MemberEntity member = memberRepository.findById(searchId).orElse(null);
        
        if (member != null) {
            // 비밀번호 비교
            return passwordEncoder.matches(password, member.getMemberPassword());
        }
        return false; // 사용자 없음
    }
    
    
}
