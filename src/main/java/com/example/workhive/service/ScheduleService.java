package com.example.workhive.service;

import com.example.workhive.domain.dto.schedule.ScheduleDTO;
import com.example.workhive.domain.entity.schedule.ScheduleEntity;
import com.example.workhive.repository.MemberRepository;
import com.example.workhive.repository.schedule.ScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;

    // 현재 로그인한 멤버의 일정 조회
    public List<ScheduleDTO> getEventsForMember(String memberId) {
        List<ScheduleEntity> scheduleEntityList = scheduleRepository.findByMember_MemberId(memberId);  // MemberEntity의 memberId로 일정 조회
        return scheduleEntityList.stream()
                .map(entity -> new ScheduleDTO(
                        entity.getScheduleId(),
                        entity.getMember().getMemberId(),  // MemberEntity의 memberId를 가져옴
                        entity.getTitle(),
                        entity.getDescription(),
                        entity.getStartDate(),
                        entity.getEndDate(),
                        entity.getIsAllDay(),
                        entity.getCategory().getCategoryId(),  // CategoryEntity의 categoryId를 가져옴
                        entity.getCategoryNum()
                ))
                .collect(Collectors.toList());  // DTO로 변환하여 반환
    }

    // 일정 추가 로직 나중에 수정 할 예정
    public void eventSave(ScheduleDTO scheduleDTO) {
        ScheduleEntity scheduleEntity = ScheduleEntity.builder()
                .title(scheduleDTO.getTitle())
                .startDate(scheduleDTO.getStartDate())
                .endDate(scheduleDTO.getEndDate())
                .isAllDay(scheduleDTO.getIsAllDay())
                .categoryNum(scheduleDTO.getCategoryNum())
                .build();
    }


}
