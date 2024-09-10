package com.example.workhive.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 회원정보 Entity
 */
@Entity
@Table(name = "member_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDetailEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "member_num")
        private int memberNum;

        @ManyToOne
        @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false)
        private MemberEntity member;

        @ManyToOne
        @JoinColumn(name = "department_num", referencedColumnName = "department_num", nullable = false)
        private DepartmentEntity department;

        @ManyToOne
        @JoinColumn(name = "company_url", referencedColumnName = "company_url", nullable = false)
        private CompanyEntity company;

        @ManyToOne
        @JoinColumn(name = "subdep_num", referencedColumnName = "subdep_num")
        private SubDepartmentEntity subDepartment;

        @Column(name = "member_status", nullable = false, length = 50, columnDefinition = "varchar(50) default '재직'")
        private String memberStatus;

        @Column(name = "profile", nullable = false, length = 100, columnDefinition = "varchar(100) default 'icon'")
        private String profile;

        @Column(name = "hire_date")
        private LocalDate hireDate;
}
