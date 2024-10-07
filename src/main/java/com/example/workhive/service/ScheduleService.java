package com.example.workhive.service;

import com.example.workhive.domain.dto.MemberDTO;
import com.example.workhive.domain.dto.schedule.ScheduleDTO;
import com.example.workhive.domain.dto.schedule.TodayScheduleDTO;
import com.example.workhive.domain.entity.MemberDetailEntity;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.domain.entity.schedule.CategoryEntity;
import com.example.workhive.domain.entity.schedule.ScheduleEntity;
import com.example.workhive.repository.MemberDetailRepository;
import com.example.workhive.repository.MemberRepository;
import com.example.workhive.repository.CategoryRepository;
import com.example.workhive.repository.ScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional  // 예외 발생시 커밋 안하고 롤백해주는 어노테이션
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final MemberDetailRepository memberDetailRepository;

    // 현재 로그인한 멤버의 일정 조회 로직
    public Set<ScheduleDTO> getEventsForMember(String memberId, Long companyId, Long departmentId, Long teamId) {

        // 일정을 담을 HashSet 선언(중복 정보제거)
        Set<ScheduleEntity> scheduleEntitySet = new HashSet<>();

        // 회사 일정 조회
        if(companyId != null) {
            scheduleEntitySet.addAll(scheduleRepository.findByCategoryIdAndCompanyId(companyId));
        }
        // 부서 일정 조회
        if(departmentId != null) {
            scheduleEntitySet.addAll(scheduleRepository.findByCategoryIdAndDepartmentId(departmentId));
        }
        // 팀 일정 조회
        if(teamId != null) {
            scheduleEntitySet.addAll(scheduleRepository.findByCategoryIdAndTeamId(teamId));
        }
        // 개인 일정 조회(memberId로 저장된거 가져오기)
        scheduleEntitySet.addAll(scheduleRepository.findByMember_MemberId(memberId));

        // Set을 DTO로 변환
        return scheduleEntitySet.stream()
                .map(entity -> new ScheduleDTO(
                        entity.getScheduleId(),
                        entity.getMember().getMemberId(),  // MemberEntity 의 memberId를 가져옴
                        entity.getTitle(),
                        entity.getDescription(),
                        entity.getStartDate(),
                        entity.getEndDate(),
                        entity.getIsAllDay(),
                        entity.getCategory().getCategoryId(),  // CategoryEntity 의 categoryId를 가져옴
                        entity.getCategory().getColor(),       // CategoryEntity 의 categoryColor 를 가져옴
                        entity.getCategoryNum()                // 추가적인 카테고리 분류 번호
                ))
                .collect(Collectors.toSet());  // Set으로 변환하여 반환
    }

    // 일정 추가 로직
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

        // categoryNum 설정을 분리된 메서드로 처리
        Long categoryNum = getCategoryNum(scheduleDTO.getCategoryId(), memberEntity, memberDetail);
        scheduleDTO.setCategoryNum(categoryNum);

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

    // 일정 수정하기
    public void updateEvent(Long id, ScheduleDTO scheduleDTO) {
        // DB에서 수정할 일정 조회
        ScheduleEntity scheduleEntity = scheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("일정을 찾을 수 없습니다."));
        // 일정 소유자 확인
        MemberEntity memberEntity = scheduleEntity.getMember();

        // 일정 데이터 업데이트
        scheduleEntity.setTitle(scheduleDTO.getTitle());
        scheduleEntity.setDescription(scheduleDTO.getDescription());
        scheduleEntity.setStartDate(scheduleDTO.getStartDate());
        scheduleEntity.setEndDate(scheduleDTO.getEndDate());
        scheduleEntity.setIsAllDay(scheduleDTO.getIsAllDay());

        // 카테고리 업데이트
        CategoryEntity categoryEntity = categoryRepository.findById(scheduleDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다."));
        scheduleEntity.setCategory(categoryEntity);

        // 카테고리 번호 설정 (필요에 따라)
        scheduleEntity.setCategoryNum(getCategoryNum(scheduleDTO.getCategoryId(), memberEntity, null));

        scheduleRepository.save(scheduleEntity);    // 수정된 일정 저장
    }

    // 일정 삭제 구현
    public void deleteEvent(Long id) {
        // 일정이 존재하는지 확인 후 삭제
        ScheduleEntity event = scheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));
        scheduleRepository.delete(event);  // 일정 삭제
    }

    // 특정 일정이 로그인한 사용자 소유인지 확인하는 메서드
    public boolean isEventOwner(Long eventId, String memberId) {
        return scheduleRepository.existsByScheduleIdAndMember_MemberId(eventId, memberId);
    }

    // 카테고리 번호 설정을 처리하는 메서드 분리, Long 타입은 switch 사용불가!
    private Long getCategoryNum(Long categoryId, MemberEntity memberEntity, MemberDetailEntity memberDetail) {
        if (categoryId == 1) {
            return null;
        } else if (categoryId == 2) {
            if (memberEntity.getCompany() != null) {
                return memberEntity.getCompany().getCompanyId();
            } else {
                throw new IllegalStateException("해당 멤버의 회사 정보가 없습니다.");
            }
        } else if (categoryId == 3) {
            if (memberDetail != null) {
                return memberDetail.getDepartment().getDepartmentId();
            } else {
                throw new IllegalStateException("해당 멤버의 부서 정보가 없습니다.");
            }
        } else if (categoryId == 4) {
            if (memberDetail != null) {
                return memberDetail.getTeam().getTeamId();
            } else {
                throw new IllegalStateException("해당 멤버의 팀 정보가 없습니다.");
            }
        } else {
            throw new IllegalArgumentException("유효하지 않은 카테고리입니다.");
        }
    }

    public String getUserRole(String memberId) {
        // memberId로 회원 정보를 조회하고 role을 가져옴
        MemberEntity member = memberRepository.findByMemberId(memberId);
        // RoleEnum에서 문자열로 변환
        return member.getRole().name();
    }

    // 홈 화면에 띄울 오늘의 일정 가져오기
    public List<TodayScheduleDTO> getTodaySchedule() {
        LocalDate today = LocalDate.now();  // 오늘 날짜 (시간 제외)

        // ScheduleEntity 리스트를 가져옴
        List<ScheduleEntity> scheduleEntities = scheduleRepository.findSchedulesByDate(today);
        log.debug("받아온 scheduleEntities 확인: {}", scheduleEntities);

        // ScheduleDTO로 변환하여 필요한 필드만 TodayScheduleDTO로 변환
        return scheduleEntities.stream()
                .map(entity -> convertToTodayScheduleDto(entity))  // 엔티티 -> TodayScheduleDTO 변환
                .collect(Collectors.toList());
    }
    // 엔티티를 TodayScheduleDTO로 변환하는 메서드
    private TodayScheduleDTO convertToTodayScheduleDto(ScheduleEntity entity) {
        return TodayScheduleDTO.builder()
                .title(entity.getTitle())            // 일정 제목
                .startDate(entity.getStartDate())    // 일정 시간
                .build();
    }

}

