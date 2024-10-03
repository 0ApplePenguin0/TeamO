package com.example.workhive.domain.dto.meet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingRoomDTO {
    private Long roomId;
    private String roomName;
    private String location;
    private int capacity;
    private String roomStatus; // String 타입으로 변경
}
