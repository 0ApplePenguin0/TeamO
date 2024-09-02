package com.example.workhive.security;


import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 사용자 인증 처리
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticatedUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {

        //없으면 예외
        MemberEntity entity = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("회원정보가 없습니다."));

        //있으면 그 정보로 UserDetails 객체 생성하여 리턴
        AuthenticatedUser user = AuthenticatedUser.builder()
                .memberId(entity.getMemberId())
                .memberPassword(entity.getMemberPassword()) // 비밀번호는 암호화 하지 않고 DB에서 직접 가져옴
                .build();
        log.info("로그인 시도 : {}", id);
        //여기에서 데이터베이스에서 사용자 정보를 로드합니다.
        return user;
   }

}
