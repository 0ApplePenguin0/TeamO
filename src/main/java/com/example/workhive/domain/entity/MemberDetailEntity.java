package com.example.workhive.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 회원 상세 정보 Entity
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
    @Column(name = "member_detail_id")  // member_detail_id에 대응
    private Long memberDetailId;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false)  // member_id에 대응
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "position_id", referencedColumnName = "position_id", nullable = false)  // position_id에 대응
    private PositionEntity position;

    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "department_id", nullable = false)  // department_id에 대응
    private DepartmentEntity department;

    @ManyToOne
    @JoinColumn(name = "team_id", referencedColumnName = "team_id", nullable = false)  // team_id에 대응
    private TeamEntity team;

    @Column(name = "status", nullable = false, length = 100)  // status에 대응 (퇴직, 출장, 출산휴가 등)
    private String status;

    @Column(name = "profile_url", length = 255)  // profile_url에 대응 (프로필 사진 URL)
    private String profileUrl;

    @Column(name = "hire_date")  // hire_date에 대응
    private LocalDate hireDate;
}
