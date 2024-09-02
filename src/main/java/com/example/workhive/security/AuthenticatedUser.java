package com.example.workhive.security;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 회원 인증 정보 객체
 */

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthenticatedUser implements UserDetails {

    private String memberId;     // 사원 ID
    private String memberPassword; // 사원 비밀번호


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return memberId;
    }

    @Override
    public String getPassword() {
        return memberPassword;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
