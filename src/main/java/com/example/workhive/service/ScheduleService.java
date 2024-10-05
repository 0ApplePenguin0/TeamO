package com.example.workhive.service;

import com.example.workhive.domain.dto.schedule.ScheduleDTO;
import com.example.workhive.domain.entity.MemberDetailEntity;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.domain.entity.schedule.CategoryEntity;
import com.example.workhive.domain.entity.schedule.ScheduleEntity;
import com.example.workhive.repository.MemberDetailRepository;
import com.example.workhive.repository.MemberRepository;
import com.example.workhive.repository.schedule.CategoryRepository;
import com.example.workhive.repository.schedule.ScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final MemberDetailRepository memberDetailRepository;

    // 현재 로그인한 멤버의 일정 조회 로직
    public List<ScheduleDTO> getEventsForMember(String memberId, Long companyId, Long departmentId, Long teamId) {
        
        // 일정을 담을 HashSet 선언
        Set<ScheduleEntity> scheduleEntitySet = new HashSet<>();

        // 회사 일정 조회
        scheduleEntitySet.addAll(scheduleRepository.findByCompanyId(companyId));

        // 부서 일정 조회
        scheduleEntitySet.addAll(scheduleRepository.findByDepartmentId(departmentId));



        List<ScheduleEntity> scheduleEntityList = scheduleRepository.findByMember_MemberId(memberId);  // MemberEntity의 memberId로 일정 조회
        log.debug("서비스에서 확인 DB에서 찾아서 가져온 정보 : {}", scheduleEntityList);

        // categoryId 에 따라 추가 조건으로 일정 조회 (회사, 부서, 팀)
        List<ScheduleEntity> additionalSchedules = new ArrayList<>();

        return scheduleEntityList.stream()
                .map(entity -> new ScheduleDTO(
                        entity.getScheduleId(),
                        entity.getMember().getMemberId(),  // MemberEntity 의 memberId를 가져옴
                        entity.getTitle(),
                        entity.getDescription(),
                        entity.getStartDate(),
                        entity.getEndDate(),
                        entity.getIsAllDay(),
                        entity.getCategory().getCategoryId(),  // CategoryEntity 의 categoryId를 가져옴
                        entity.getCategory().getColor(), // CategoryEntity 의 categoryColor 를 가져옴
                        entity.getCategoryNum()
                ))
                .collect(Collectors.toList());  // DTO로 변환하여 반환
    }

    // 일정 추가 로직
    @Transactional  // 예외 발생시 커밋 안하고 롤백해주는 어노테이션
    public void addEvent(ScheduleDTO scheduleDTO) {
        log.debug("넘어온 memberId: " + scheduleDTO.getMemberId());    // 넘어온 id값 확인

        // memberId 를 이용해 DB 에서 회원 정보를 조회
        MemberEntity memberEntity = memberRepository.findById(scheduleDTO.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException(scheduleDTO.getMemberId()
                + " 해당 아이디로 된 정보 찾기 불가능!"));
        log.debug("찾아온 member정보: {}", memberEntity);

        // categoryId를 통해 DB에서 카테고리 정보 조회
        log.debug("categoruId 잘 넘어 왔나 확인: {}", scheduleDTO.getCategoryId());
        CategoryEntity categoryEntity = categoryRepository.findById(scheduleDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException(scheduleDTO.getCategoryId()
                        + " 해당 카테고리로 된 정보 찾기 불가능!"));
        log.debug("찾아온 category정보: {}", categoryEntity);

        // memberId를 이용해서 MemberDetail 정보 조회
        MemberDetailEntity memberDetail = memberDetailRepository.findByMember_MemberId(scheduleDTO.getMemberId());
        log.debug("가져온 MemberDetail: {}", memberDetail);

        // categoryId에 따라 달라지는 categoryNum 찾아서 넣기, Long 타입은 switch 사용불가!
        if (scheduleDTO.getCategoryId() == 1) {
            scheduleDTO.setCategoryNum(null);
        }else if (scheduleDTO.getCategoryId() == 2) {
            if (memberEntity.getCompany() != null) {
                scheduleDTO.setCategoryNum(memberEntity.getCompany().getCompanyId());
            } else {
                throw new IllegalStateException("해당 멤버의 화사 정보가 없습니다.");
            }
        }else if (scheduleDTO.getCategoryId() == 3) {
            if (memberDetail!= null) {
                scheduleDTO.setCategoryNum(memberDetail.getDepartment().getDepartmentId());
            } else {
                throw new IllegalStateException("해당 멤버의 부서 정보가 없습니다.");
            }
        }else if (scheduleDTO.getCategoryId() == 4) {
            if(memberDetail != null) {
                scheduleDTO.setCategoryNum(memberDetail.getTeam().getTeamId());
            } else {
                throw new IllegalStateException("해당 멤버의 팀 정보가 없습니다.");
            }
        }
        ScheduleEntity scheduleEntity = ScheduleEntity.builder()
                .member(memberEntity)   // 조회한 정보 넣기
                .title(scheduleDTO.getTitle())  // 제목 설정
                .description(scheduleDTO.getDescription())  // 설명 설정
                .startDate(scheduleDTO.getStartDate())  // 시작 날짜 설정
                .endDate(scheduleDTO.getEndDate())  // 종료 날짜 설정
                .isAllDay(scheduleDTO.getIsAllDay())    // 하루 종일 여부 설정
                .category(categoryEntity)   // 조회한 카테고리 정보
                .categoryNum(scheduleDTO.getCategoryNum())  // 카테고리 번호 설정
                .build();

        scheduleRepository.save(scheduleEntity);    // 일정저장
    }
}
