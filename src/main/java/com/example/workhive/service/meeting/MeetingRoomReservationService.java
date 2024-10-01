package com.example.workhive.service.meeting;

import com.example.workhive.domain.entity.MeetingRoom.MeetingRoomEntity;
import com.example.workhive.domain.entity.MeetingRoom.MeetingRoomReservationEntity;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.repository.MemberRepository;
import com.example.workhive.repository.meeting.MeetingRoomRepository;
import com.example.workhive.repository.meeting.MeetingRoomReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingRoomReservationService {
    private final MeetingRoomReservationRepository reservationRepository;
    private final MeetingRoomRepository meetingRoomRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public boolean reserveMeetingRoom(Long roomId, String memberId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            MeetingRoomEntity meetingRoom = meetingRoomRepository.findById(roomId)
                    .orElseThrow(() -> new RuntimeException("Meeting room not found"));

            MemberEntity member = memberRepository.findByMemberId(memberId);


            // 예약 가능 여부 확인
            boolean exists = reservationRepository.existsByMeetingRoomRoomIdAndStartTimeLessThanAndEndTimeGreaterThan(
                    roomId, endTime, startTime);

            if (exists) {
                return false; // 이미 예약된 시간대
            }

            MeetingRoomReservationEntity reservation = MeetingRoomReservationEntity.builder()
                    .meetingRoom(meetingRoom)
                    .member(member)
                    .company(meetingRoom.getCompany())
                    .startTime(startTime)
                    .endTime(endTime)
                    .status(MeetingRoomReservationEntity.ReservationStatus.CONFIRMED)
                    .build();

            reservationRepository.save(reservation);

            return true;
        } catch (DataIntegrityViolationException | ObjectOptimisticLockingFailureException e) {
            // 예약 중복 또는 낙관적 락 충돌 발생 시
            return false;
        }
    }

    public List<MeetingRoomReservationEntity> getReservationsByRoomIdAndDate(Long roomId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59, 999999999);

        return reservationRepository.findByMeetingRoomRoomIdAndStartTimeBetween(roomId, startOfDay, endOfDay);
    }
}