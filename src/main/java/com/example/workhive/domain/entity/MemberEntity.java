package com.example.workhive.domain.entity;

import com.example.workhive.domain.entity.MeetingRoom.MeetingRoomReservationEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEntity {
    @Id
    @ToString.Exclude
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

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MemberDetailEntity memberDetail; // member_detail 엔티티와의 연관 관계

    // 역할을 정의하는 ENUM
    public enum RoleEnum {
        ROLE_USER,
        ROLE_EMPLOYEE,
        ROLE_MANAGER,
        ROLE_ADMIN
    }

        // 예약한 회의실 예약 리스트
        @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
        private List<MeetingRoomReservationEntity> reservations;

        // equals와 hashCode는 memberId만 사용
        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof MemberEntity)) return false;
                MemberEntity that = (MemberEntity) o;
                return memberId != null && memberId.equals(that.getMemberId());
        }

        @Override
        public int hashCode() {
                return getClass().hashCode();
        }
}
