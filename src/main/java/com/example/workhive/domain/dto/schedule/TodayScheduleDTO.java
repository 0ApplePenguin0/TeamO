package com.example.workhive.domain.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodayScheduleDTO {

    private String title;  // 일정 제목
    private LocalDateTime startDate;  // 일정 시작 시간
}
