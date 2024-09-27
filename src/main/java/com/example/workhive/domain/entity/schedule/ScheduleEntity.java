package com.example.workhive.domain.entity.schedule;

import com.example.workhive.domain.entity.CategoryEntity;
import com.example.workhive.domain.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "schedule")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;  // 일정 ID (기본 키)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(name = "FK_schedule_member"))
    private MemberEntity member;  // 회원 (외래키)

    @Column(name = "title", length = 255)
    private String title;  // 일정 제목

    @Column(name = "description", length = 255)
    private String description;  // 일정 설명

    @Column(name = "start_date")
    private LocalDate startDate;  // 시작 날짜

    @Column(name = "end_date")
    private LocalDate endDate;  // 종료 날짜

    @Column(name = "is_all_day")
    private Boolean isAllDay;  // 당일 일정 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "FK_schedule_category"))
    private CategoryEntity category;  // 카테고리 (외래키)

    @Column(name = "category_num")
    private Long categoryNum;  // 추가적인 카테고리 분류 번호 (null 가능)

}
