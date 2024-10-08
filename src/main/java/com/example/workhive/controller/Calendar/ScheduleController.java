package com.example.workhive.controller.Calendar;

import com.example.workhive.domain.dto.MemberDTO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@RestController    // api 요청을 처리해주는 controller 에 어노테이션
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;  // 의존성 주입 final
    private final MemberService memberService;

    // 로그인 중인 id의 모든 일정 가져오는 API 수정예정
    @GetMapping("/events")
    public ResponseEntity<Set<ScheduleDTO>> getEvents(HttpSession session, HttpServletResponse response) throws IOException {
        // 세션에서 id 가져오기
        String memberId = (String) session.getAttribute("memberId");
        Long companyId = (Long) session.getAttribute("companyId");
        Long departmentId = (Long) session.getAttribute("departmentId");
        Long teamId = (Long) session.getAttribute("teamId");

        log.debug("컨트롤러 확인 세션에서 가져온 memberId: {}", memberId);
        log.debug("컨트롤러 확인 세션에서 가져온 companyId: {}", companyId);
        log.debug("컨트롤러 확인 세션에서 가져온 departmentId: {}", departmentId);
        log.debug("컨트롤러 확인 세션에서 가져온 teamId: {}", teamId);

        // id가 null 이면 login 화면으로
        if (memberId == null) {
            response.sendRedirect("/login");
            return null;
        }

        Set<ScheduleDTO> events = scheduleService.getEventsForMember(memberId, companyId, departmentId, teamId);  // 일정 조회
        return ResponseEntity.ok(events);  // 조회된 일정 반환
    }

    // 일정 추가 API
    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addEvent(@RequestBody ScheduleDTO scheduleDTO, HttpSession session) {
        String memberId = (String) session.getAttribute("memberId");

        scheduleDTO.setMemberId(memberId);  // 세션에 있는 memberId을 DTO에 셋팅
        scheduleService.addEvent(scheduleDTO);  // 일정 추가로직(서버)으로 넘기기

        // 성공 메시지를 포함한 응답 반환
        Map<String, String> response = new HashMap<>();
        response.put("message", "Event added successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 일정 수정 API
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, String>> updateEvent(@PathVariable Long id, @RequestBody ScheduleDTO scheduleDTO, HttpSession session) {
        String memberId = (String) session.getAttribute("memberId");

        // 수정하려는 일정의 회원이 현재 로그인한 사용자인지 확인
        if (!scheduleService.isEventOwner(id, memberId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "권한이 없습니다!"));
        }

        // 일정 수정 로직 호출
        scheduleDTO.setMemberId(memberId);  // 세션에 있는 memberId을 DTO에 �����
        scheduleService.updateEvent(id, scheduleDTO);

        // 성공 메시지를 포함한 응답 반환
        return ResponseEntity.ok(Map.of("message", "Event updated successfully"));
    }

    // 일정 삭제 API
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteEvent(@PathVariable Long id, HttpSession session) {
        String memberId = (String) session.getAttribute("memberId");

        // 삭제하려는 일정의 회원이 현재 로그인한 사용자인지 확인
        if (!scheduleService.isEventOwner(id, memberId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized to delete this event."));
        }

        // 일정 삭제 로직 호출
        scheduleService.deleteEvent(id);

        // 성공 메시지를 포함한 응답 반환
        return ResponseEntity.ok(Map.of("message", "Event deleted successfully"));
    }

    // 로그인한 회원의 role 가져오기
    @GetMapping("/role")
    public ResponseEntity<Map<String, String>> getUserRole(HttpSession session) {
        String memberId = (String) session.getAttribute("memberId");

        // memberId가 없으면 로그인 화면으로 리다이렉트
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User is not logged in"));
        }

        // 서비스에서 회원의 role을 가져옴
        String role = scheduleService.getUserRole(memberId);

        if (role == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Role not found for user"));
        }
        log.debug("가져온 role: {}", role);
        return ResponseEntity.ok(Map.of("role", role));
    }
}
