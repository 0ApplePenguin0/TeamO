package com.example.workhive.service.meeting;

import com.example.workhive.domain.dto.meet.MeetingRoomDTO;
import com.example.workhive.domain.dto.meet.MeetingRoomDetailDTO;
import com.example.workhive.domain.dto.meet.ReservationDTO;
import com.example.workhive.domain.entity.CompanyEntity;
import com.example.workhive.domain.entity.MeetingRoom.MeetingRoomEntity;
import com.example.workhive.domain.entity.MeetingRoom.MeetingRoomReservationEntity;
import com.example.workhive.domain.entity.MeetingRoom.RoomStatus;
import com.example.workhive.repository.CompanyRepository;
import com.example.workhive.repository.meeting.MeetingRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetingRoomService {
    private final MeetingRoomRepository meetingRoomRepository;
    private final CompanyRepository companyRepository;

    public List<MeetingRoomDTO> getMeetingRoomsByCompanyId(Long companyId) {
        List<MeetingRoomEntity> meetingRooms = meetingRoomRepository.findByCompanyCompanyId(companyId);
        return meetingRooms.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private MeetingRoomDTO convertToDTO(MeetingRoomEntity entity) {
        String status = entity.getRoomStatus().name().trim();
        log.debug("잘 들어 오나: roomId={}, roomName={}, roomStatus='{}'",
                entity.getRoomId(), entity.getRoomName(), status);
        return MeetingRoomDTO.builder()
                .roomId(entity.getRoomId())
                .roomName(entity.getRoomName())
                .location(entity.getLocation())
                .capacity(entity.getCapacity())
                .roomStatus(status) // enum -> String
                .build();
    }

    public MeetingRoomDetailDTO getMeetingRoomDetail(Long roomId) {
        MeetingRoomEntity meetingRoom = meetingRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Meeting room not found"));

        List<MeetingRoomReservationEntity> reservations = meetingRoom.getReservations();
        List<ReservationDTO> reservationDTOs = reservations.stream().map(reservation -> ReservationDTO.builder()
                .reservationId(reservation.getReservationId())
                .memberId(reservation.getMember().getMemberId())
                .memberName(reservation.getMember().getMemberName())
                .status(reservation.getStatus().name())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .build()).collect(Collectors.toList());

        return MeetingRoomDetailDTO.builder()
                .roomId(meetingRoom.getRoomId())
                .roomName(meetingRoom.getRoomName())
                .location(meetingRoom.getLocation())
                .capacity(meetingRoom.getCapacity())
                .roomStatus(meetingRoom.getRoomStatus().name()) // enum -> String
                .companyName(meetingRoom.getCompany().getCompanyName())
                .reservations(reservationDTOs)
                .build();
    }
    private ReservationDTO convertToReservationDTO(MeetingRoomReservationEntity entity) {
        return ReservationDTO.builder()
                .reservationId(entity.getReservationId())
                .memberName(entity.getMember().getMemberName())
                .status(entity.getStatus().name())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .build();
    }

    public MeetingRoomEntity addMeetingRoom(Long companyId, String roomName, String location, int capacity) {
        CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        MeetingRoomEntity meetingRoom = MeetingRoomEntity.builder()
                .company(company)
                .roomName(roomName)
                .location(location)
                .capacity(capacity)
                .roomStatus(RoomStatus.AVAILABLE)
                .build();

        return meetingRoomRepository.save(meetingRoom);
    }

    public MeetingRoomEntity updateMeetingRoom(Long roomId, String roomName, String location, int capacity, RoomStatus status) {
        MeetingRoomEntity meetingRoom = meetingRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Meeting room not found"));

        meetingRoom.setRoomName(roomName);
        meetingRoom.setLocation(location);
        meetingRoom.setCapacity(capacity);
        meetingRoom.setRoomStatus(status);

        return meetingRoomRepository.save(meetingRoom);
    }

    public void deleteMeetingRoom(Long roomId) {
        MeetingRoomEntity meetingRoom = meetingRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Meeting room not found"));
        meetingRoomRepository.delete(meetingRoom);
    }
}
