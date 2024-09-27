package com.example.workhive.controller.Calendar;

import com.example.workhive.domain.dto.MemberDTO;
import com.example.workhive.domain.dto.MemoDTO;
import com.example.workhive.domain.dto.schedule.ScheduleDTO;
import com.example.workhive.service.MemberService;
import com.example.workhive.service.ScheduleService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController    // api 요청을 처리해주는 controller 에 어노테이션
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;  // 의존성 주입 final
    private final MemberService memberService;

    // 로그인 중인 id의 모든 일정 가져오기
    @GetMapping("/events")
    public List<ScheduleDTO> getEvents() {
        return null;
    }
}
