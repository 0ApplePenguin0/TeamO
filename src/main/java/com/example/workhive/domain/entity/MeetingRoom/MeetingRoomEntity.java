package com.example.workhive.domain.entity.MeetingRoom;

import com.example.workhive.domain.entity.CompanyEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "meeting_room")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "company_id", nullable = false)
    private CompanyEntity company;

    @Column(name = "room_name", length = 100, nullable = false)
    private String roomName;

    @Column(name = "location", length = 255, nullable = false)
    private String location;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_status", length = 50, nullable = false)
    private RoomStatus roomStatus = RoomStatus.AVAILABLE;

    // 해당 회의실의 예약 목록
    @OneToMany(mappedBy = "meetingRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude // 예약 목록을 toString에서 제외
    private List<MeetingRoomReservationEntity> reservations;
}
