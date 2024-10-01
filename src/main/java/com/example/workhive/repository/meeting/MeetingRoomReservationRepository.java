package com.example.workhive.repository.meeting;

import com.example.workhive.domain.entity.MeetingRoom.MeetingRoomReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MeetingRoomReservationRepository extends JpaRepository<MeetingRoomReservationEntity, Long> {
    List<MeetingRoomReservationEntity> findByMeetingRoomRoomIdAndStartTimeBetween(
            Long roomId, LocalDateTime start, LocalDateTime end);

    boolean existsByMeetingRoomRoomIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long roomId, LocalDateTime endTime, LocalDateTime startTime);
}
