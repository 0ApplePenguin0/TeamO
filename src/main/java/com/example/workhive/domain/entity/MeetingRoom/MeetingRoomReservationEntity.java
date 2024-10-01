package com.example.workhive.domain.entity.MeetingRoom;

import com.example.workhive.domain.entity.CompanyEntity;
import com.example.workhive.domain.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "meeting_room_reservation", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"room_id", "start_time", "end_time"})
})//uniqueConstraints 컬럼의 값을 중복시키지 않는다.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingRoomReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long reservationId;

    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "room_id", nullable = false)
    private MeetingRoomEntity meetingRoom;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false)
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "company_id", nullable = false)
    private CompanyEntity company;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    private ReservationStatus status = ReservationStatus.CONFIRMED;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "cancel_reason", length = 255)
    private String cancelReason;

    @Version // 낙관적 락을 위한 버전 필드 추가
    private Long version;

    public enum ReservationStatus {
        CONFIRMED,
        CANCELED
    }
}