package com.example.workhive.service;

import com.example.workhive.domain.dto.MemberDTO;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

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



    /*가입처리*/
    public void join(MemberDTO dto) {
/*        MemberEntity entity = MemberEntity.builder()
                .memberId(dto.getMemberId())
                //passEncoder -> db에 패스워드를 암호화 시키는 메서드
                .memberPassword(passwordEncoder.encode(dto.getMemberPassword()))
                .memberName(dto.getMemberName())
                .email(dto.getEmail())
                .build();*/

        MemberEntity entity = new MemberEntity();
        entity.setMemberId(dto.getMemberId());
        entity.setMemberName(dto.getMemberName());
        entity.setMemberPassword(passwordEncoder.encode(dto.getMemberPassword()));
        entity.setEmail(dto.getEmail());

        //DB에 저장
        memberRepository.save(entity);
    }

    // 모든 회원을 불러오는 메서드
	/*
	 * public List<MemberDTO> getAllMembers() { List<MemberEntity> members =
	 * memberRepository.findAll(); return members.stream() .map(member -> new
	 * MemberDTO( member.getMemberId(), member.getMemberPassword(),
	 * member.getMemberName(), member.getEmail(), member.getRoleName(),
	 * member.getCompany()) .collect(Collectors.toList())); }
	 */
}
