package com.example.workhive.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEntity {

    @Id
    @Column(name = "member_id", length = 100)
    private String memberId; // 로그인 아이디로 사용되는 회원 ID

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "member_name", nullable = false, length = 50)
    private String memberName;

    @Column(name = "member_password", nullable = false)
    private String memberPassword;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role; // MemberRole로 수정

    @ManyToOne
    @JoinColumn(name = "company_id")
    private CompanyEntity company;

    public enum Role { // 열거형 정의
        ROLE_USER,
        ROLE_EMPLOYEE,
        ROLE_MANAGER,
        ROLE_ADMIN
    }
}