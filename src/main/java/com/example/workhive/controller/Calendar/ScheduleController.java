package com.example.workhive.controller.Calendar;

import com.example.workhive.domain.dto.MemberDTO;
import com.example.workhive.domain.dto.MemoDTO;
import com.example.workhive.domain.dto.schedule.ScheduleDTO;
import com.example.workhive.service.MemberService;
import com.example.workhive.service.ScheduleService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController    // api 요청을 처리해주는 controller 에 어노테이션
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;  // 의존성 주입 final
    private final MemberService memberService;

    // 로그인 중인 id의 모든 일정 가져오기 수정 예정
    @GetMapping("/events")
    public ResponseEntity<List<ScheduleDTO>> getEvents(HttpSession session, HttpServletResponse response) throws IOException {
        // 세션에서 id 가져오기
        String memberId = (String) session.getAttribute("memberId");

        if (memberId == null) {
            response.sendRedirect("/login");
            return null;
        }

        List<ScheduleDTO> events = scheduleService.getEventsForMember(memberId);  // 일정 조회
        return ResponseEntity.ok(events);  // 조회된 일정 반환
    }

}
