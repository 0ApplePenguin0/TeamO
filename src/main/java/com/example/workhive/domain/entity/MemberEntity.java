package com.example.workhive.domain.entity;

import com.example.workhive.domain.entity.attendance.AttendanceEntity;
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
        private String memberId; // 회원 ID

        @Column(name = "member_name", length = 50, nullable = false)
        private String memberName; // 회원 이름

        @Column(name = "email", length = 50, nullable = false, unique = true)
        private String email; // 이메일

        @Column(name = "member_password", length = 100, nullable = false)
        private String memberPassword; // 비밀번호

        @Enumerated(EnumType.STRING) // ENUM 타입으로 설정
        @Column(name = "role", nullable = false, columnDefinition = "ENUM('ROLE_USER', 'ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_ADMIN') DEFAULT 'ROLE_USER'")
        private RoleEnum role = RoleEnum.ROLE_USER; // 회원 역할

        @ManyToOne // 회사와의 관계 설정
        @JoinColumn(name = "company_id", referencedColumnName = "company_id", nullable = true) // 외래키 설정
        private CompanyEntity company; // 회사 엔티티

        // 역할을 정의하는 ENUM
        public enum RoleEnum {
                ROLE_USER,
                ROLE_EMPLOYEE,
                ROLE_MANAGER,
                ROLE_ADMIN
        }
}
