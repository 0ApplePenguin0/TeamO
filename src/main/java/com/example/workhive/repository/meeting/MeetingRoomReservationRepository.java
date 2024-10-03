package com.example.workhive.repository.meeting;

import com.example.workhive.domain.entity.MeetingRoom.MeetingRoomReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MeetingRoomReservationRepository extends JpaRepository<MeetingRoomReservationEntity, Long> {

    // 기존 예약 목록 조회 메서드 (모든 상태)
    List<MeetingRoomReservationEntity> findByMeetingRoomRoomIdAndStartTimeBetween(
            Long roomId, LocalDateTime start, LocalDateTime end);

    // CONFIRMED 상태인 예약 목록 조회 메서드 (추가)
    @Query("""
    SELECT r FROM MeetingRoomReservationEntity r
    WHERE r.meetingRoom.roomId = :roomId
    AND r.startTime BETWEEN :start AND :end
    AND r.status = 'CONFIRMED'
    """)
    List<MeetingRoomReservationEntity> findConfirmedReservationsByRoomIdAndStartTimeBetween(
            @Param("roomId") Long roomId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
SELECT COUNT(r) > 0 FROM MeetingRoomReservationEntity r
WHERE r.meetingRoom.roomId = :roomId
AND r.status = 'CONFIRMED'
AND r.startTime < :endTime
AND r.endTime > :startTime
AND NOT (r.endTime = :startTime)
""")
    boolean existsOverlappingReservation(
            @Param("roomId") Long roomId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
    }