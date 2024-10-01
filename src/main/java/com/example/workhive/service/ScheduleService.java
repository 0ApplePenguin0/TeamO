package com.example.workhive.service;

import com.example.workhive.domain.dto.schedule.ScheduleDTO;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.domain.entity.schedule.CategoryEntity;
import com.example.workhive.domain.entity.schedule.ScheduleEntity;
import com.example.workhive.repository.MemberRepository;
import com.example.workhive.repository.schedule.CategoryRepository;
import com.example.workhive.repository.schedule.ScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    // 현재 로그인한 멤버의 일정 조회 로직
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

    // 일정 추가 로직
    @Transactional  // 예외 발생시 커밋 안하고 롤백해주는 어노테이션
    public void addEvent(ScheduleDTO scheduleDTO) {
        // memberId 를 이용해 DB 에서 회원 정보를 조회
        MemberEntity memberEntity = memberRepository.findById(scheduleDTO.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException(scheduleDTO.getMemberId()
                + " 해당 아이디로 된 정보 찾기 불가능!"));

        // categoryId를 통해 DB에서 카테고리 정보 조회
        CategoryEntity categoryEntity = categoryRepository.findById(scheduleDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException(scheduleDTO.getCategoryId()
                        + " 해당 카테고리로 된 정보 찾기 불가능!"));

        ScheduleEntity scheduleEntity = ScheduleEntity.builder()
                .member(memberEntity)   // 조회한 정보 넣기
                .title(scheduleDTO.getTitle())  // 제목 설정
                .description(scheduleDTO.getDescription())  // 설명 설정
                .startDate(scheduleDTO.getStartDate())  // 시작 날짜 설정
                .endDate(scheduleDTO.getEndDate())  // 종료 날짜 설정
                .isAllDay(scheduleDTO.getIsAllDay())    // 하루 종일 여부 설정
                .category(categoryEntity)   // 조회한 카테고리 정보
//                .categoryNum(scheduleDTO.getCategoryNum())  // 카테고리 번호 설정
                .build();
    }
}
