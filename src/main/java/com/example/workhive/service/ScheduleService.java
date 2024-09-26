package com.example.workhive.service;

import com.example.workhive.domain.dto.schedule.ScheduleDTO;
import com.example.workhive.repository.MemberRepository;
import com.example.workhive.repository.schedule.ScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;

    // 현재 로그인 중 인 아이디 추출
    public List<ScheduleDTO> getCurrentUser() {
        return null;
    }

    // 현재 로그인 중 인 아이디 일정 추출
    public List<ScheduleDTO> getEvents() {
        return null;
    }

}
