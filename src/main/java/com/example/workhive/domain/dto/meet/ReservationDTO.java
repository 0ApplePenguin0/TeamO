package com.example.workhive.domain.dto.meet;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {
    private Long reservationId;
    private String memberId;
    private String memberName;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
