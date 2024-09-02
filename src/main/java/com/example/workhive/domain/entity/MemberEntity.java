package com.example.workhive.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 회원정보 Entity
 */
@Entity
@Table(name = "members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEntity {

        @Id
        @Column(name = "member_id", length = 50)
        private String memberId;

        @Column(name = "member_password", length = 100, nullable = false)
        private String memberPassword;

        @Column(name = "member_name", length = 50, nullable = false)
        private String memberName;

        @Column(name = "email", length = 100)
        private String email;

        @Column(name = "rolename", columnDefinition="varchar(50) default  ' ROLE_EMPLOYEE' check(rolename in ('ROLE_EMPLOYEE', 'ROLE_ADMIN', 'ROLE_MANAGER')) nullable = false")
                private String roleName = "ROLE_EMPLOYEE";
}
