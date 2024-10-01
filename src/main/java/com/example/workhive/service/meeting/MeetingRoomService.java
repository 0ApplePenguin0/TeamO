package com.example.workhive.service.meeting;

import com.example.workhive.domain.entity.CompanyEntity;
import com.example.workhive.domain.entity.MeetingRoom.MeetingRoomEntity;
import com.example.workhive.repository.CompanyRepository;
import com.example.workhive.repository.meeting.MeetingRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingRoomService {
    private final MeetingRoomRepository meetingRoomRepository;
    private final CompanyRepository companyRepository;

    public List<MeetingRoomEntity> getMeetingRoomsByCompanyId(Long companyId) {
        return meetingRoomRepository.findByCompanyCompanyId(companyId);
    }

    public MeetingRoomEntity addMeetingRoom(Long companyId, String roomName, String location, int capacity) {
        CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        MeetingRoomEntity meetingRoom = MeetingRoomEntity.builder()
                .company(company)
                .roomName(roomName)
                .location(location)
                .capacity(capacity)
                .roomStatus(MeetingRoomEntity.RoomStatus.AVAILABLE)
                .build();

        return meetingRoomRepository.save(meetingRoom);
    }
}