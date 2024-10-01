package com.example.workhive.domain.entity.MeetingRoom;

import com.example.workhive.domain.entity.CompanyEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public enum RoomStatus {
        AVAILABLE,
        UNAVAILABLE
    }

    // 해당 회의실의 예약 목록
    @OneToMany(mappedBy = "meetingRoom", cascade = CascadeType.ALL)
    private List<MeetingRoomReservationEntity> reservations;
}
