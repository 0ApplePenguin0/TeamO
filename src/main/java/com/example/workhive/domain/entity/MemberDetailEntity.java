package com.example.workhive.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * 회원 상세 정보 Entity
 */
@Entity
@Table(name = "member_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDetailEntity {

        @Id
        @ToString.Exclude
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "member_detail_id") // 컬럼 이름 변경
        private Long memberDetailId; // ID 타입을 Long으로 변경

        @OneToOne
        @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false)
        private MemberEntity member;

        @ManyToOne
        @JoinColumn(name = "position_id", referencedColumnName = "position_id", nullable = false) // position_id로 변경
        private PositionEntity position; // Entity 이름을 PositionEntity로 변경

        @ManyToOne
        @JoinColumn(name = "department_id", referencedColumnName = "department_id", nullable = false) // department_id로 변경
        private DepartmentEntity department;

        @ManyToOne
        @JoinColumn(name = "team_id", referencedColumnName = "team_id", nullable = false) // team_id로 변경
        private TeamEntity team; // Entity 이름을 TeamEntity로 변경

        @Column(name = "status", nullable = false, length = 100, columnDefinition = "varchar(100) default '재직 중'") // 상태 컬럼 이름 변경
        private String status;

        @Column(name = "profile_url", length = 255) // profile_url로 변경
        private String profileUrl; // 필드 이름 변경

    @Column(name = "hire_date")  // hire_date에 대응
    private LocalDate hireDate;
}
