package com.example.workhive.domain.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDTO {

    private Long scheduleId;  // 일정 ID
    private String memberId;  // 회원 ID
    private String title;  // 일정 제목
    private String description;  // 일정 설명
    private String startDate;  // 시작 날짜
    private String endDate;  // 종료 날짜
    private Boolean isAllDay;  // 하루 종일 여부
    private Long categoryId; // 일정 구분
    private Long categoryNum; // 일정 구분에 따른 id들

}
