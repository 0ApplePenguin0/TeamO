package com.example.workhive.service.meeting;

import com.example.workhive.domain.entity.MeetingRoom.MeetingRoomEntity;
import com.example.workhive.domain.entity.MeetingRoom.MeetingRoomReservationEntity;
import com.example.workhive.domain.entity.MeetingRoom.RoomStatus;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.repository.MemberRepository;
import com.example.workhive.repository.meeting.MeetingRoomRepository;
import com.example.workhive.repository.meeting.MeetingRoomReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingRoomReservationService {
    private final MeetingRoomReservationRepository reservationRepository;
    private final MeetingRoomRepository meetingRoomRepository;
    private final MemberRepository memberRepository;

    @Transactional(rollbackOn = {DataIntegrityViolationException.class, ObjectOptimisticLockingFailureException.class})
    public boolean reserveMeetingRoom(Long roomId, String memberId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            MeetingRoomEntity meetingRoom = meetingRoomRepository.findById(roomId)
                    .orElseThrow(() -> new RuntimeException("Meeting room not found"));

            // Check if room is AVAILABLE
            if (meetingRoom.getRoomStatus() == RoomStatus.UNAVAILABLE) {
                log.warn("Cannot reserve an UNAVAILABLE room: {}", roomId);
                return false;
            }

            MemberEntity member = memberRepository.findByMemberId(memberId);

            boolean exists = reservationRepository.existsOverlappingReservation(roomId, startTime, endTime);

            if (exists) {
                log.warn("Reservation conflict: Room {} is already reserved during the requested time.", roomId);
                return false;
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
            log.info("Reservation successfully made for Room {} by Member {}", roomId, memberId);

            return true;
        } catch (DataIntegrityViolationException | ObjectOptimisticLockingFailureException e) {
            log.error("Reservation failed due to conflict or locking issue: ", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during reservation: ", e);
            throw e;
        }
    }

    // 예약 취소 로직
    @Transactional
    public List<Integer> cancelReservationByUser(Long reservationId, String memberId) {
        try {
            MeetingRoomReservationEntity reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new RuntimeException("Reservation not found"));

            if (!reservation.getMember().getMemberId().equals(memberId)) {
                log.warn("Member {} attempted to cancel reservation {} which does not belong to them.", memberId, reservationId);
                return null;
            }

            // 예약 상태를 '취소됨'으로 변경
            reservation.setStatus(MeetingRoomReservationEntity.ReservationStatus.CANCELED);
            reservation.setCancelReason("User canceled the reservation");
            reservationRepository.save(reservation);

            // 예약이 취소된 시간대 반환
            int startHour = reservation.getStartTime().getHour();
            int endHour = reservation.getEndTime().getHour();
            List<Integer> canceledHours = new ArrayList<>();
            for (int hour = startHour; hour < endHour; hour++) {
                canceledHours.add(hour);
            }

            log.info("Reservation {} has been canceled by member {}.", reservationId, memberId);
            return canceledHours;
        } catch (Exception e) {
            log.error("Failed to cancel reservation {}: ", reservationId, e);
            return null;
        }
    }

    // CONFIRMED 상태인 예약만 조회하도록 수정
    public List<MeetingRoomReservationEntity> getReservationsByRoomIdAndDate(Long roomId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59, 999999999);

        return reservationRepository.findConfirmedReservationsByRoomIdAndStartTimeBetween(roomId, startOfDay, endOfDay);
    }
}
