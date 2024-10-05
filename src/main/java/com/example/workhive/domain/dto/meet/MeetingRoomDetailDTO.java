package com.example.workhive.domain.dto.meet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingRoomDetailDTO {
    private Long roomId;
    private String roomName;
    private String location;
    private int capacity;
    private String roomStatus;
    private String companyName;
    private List<ReservationDTO> reservations;
}
