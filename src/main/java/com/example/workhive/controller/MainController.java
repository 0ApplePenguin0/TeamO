package com.example.workhive.controller;

import com.example.workhive.domain.dto.MemberDetailDTO;
import com.example.workhive.domain.dto.MemoDTO;
import com.example.workhive.domain.dto.MessageDTO;
import com.example.workhive.domain.dto.schedule.ScheduleDTO;
import com.example.workhive.domain.dto.schedule.TodayScheduleDTO;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.domain.entity.attendance.AttendanceEntity;
import com.example.workhive.repository.MemberRepository;
import com.example.workhive.security.AuthenticatedUser;
import com.example.workhive.service.*;
import com.example.workhive.service.AttendanceService.AttendanceService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

@Slf4j
@Controller
@RequestMapping("main")
@RequiredArgsConstructor
public class MainController {

    private final MemberService memberService;
    private final MemoService memoService;
    private final MessageService messageService;
    private final AttendanceService attendanceService;
    private final CompanyService companyService;
    private final ScheduleService scheduleService;


    @Value("${memo.pageSize}")
    int pageSize;
    @Value("${memo.linkSize}")
    int linkSize;
    @GetMapping("board")
    public String board(Model model, HttpSession session
                    ,@RequestParam(name="page", defaultValue="1") int page
                    ,@AuthenticationPrincipal AuthenticatedUser user) {
        // 현재 로그인한 유저의 ID를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberId = authentication.getName();

        // memberId를 이용해 companyId를 가져옴
        Long companyId = memberService.getCompanyIdByMemberId(memberId);

        // companyId를 이용해 companyName을 가져옴
        String companyName = companyService.getCompanyNameById(companyId);

        // 사용자의 ID를 통해 사용자의 이름 가져오기
        String memberName = memberService.getMemberName(memberId);

        // Id를 이용해 유저의 상세 정보를 조회
        MemberDetailDTO memberDetail = memberService.getMemberDetailByMemberId(memberId);
        String departmentName = memberService.getDepartmentName(memberDetail.getDepartmentId());
        String teamName = memberService.getTeamName(memberDetail.getTeamId());
        String email = memberService.getEmail(memberId);

        // 세션에 값 저장 (이미 존재하는 값이 있을 경 우 덮어쓰기)
        session.setAttribute("memberId", memberDetail.getMemberId());
        session.setAttribute("companyId", memberDetail.getCompanyId());
        session.setAttribute("positionId", memberDetail.getPositionId());
        session.setAttribute("departmentId", memberDetail.getDepartmentId());
        session.setAttribute("teamId", memberDetail.getTeamId());
        session.setAttribute("memberName", memberDetail.getMemberName());

        // 세션의 모든 값 출력
        printSessionAttributes(session);
        
        // 모델에 담아 보내주기
        model.addAttribute("companyName", companyName);
        model.addAttribute("memberName", memberName);
        model.addAttribute("departmentName", departmentName);
        model.addAttribute("teamName", teamName);
        model.addAttribute("email", email);



        // today 관련
        // 오늘 날짜 계산
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd EEEE", Locale.KOREAN);
        String todayDate = today.format(formatter);
        // 오늘의 일정 리스트
        List<TodayScheduleDTO> todayScheduleList = scheduleService.getTodaySchedule();
        log.debug("리스트 확인 {} ", todayScheduleList);
        // 모델에 데이터 추가
        model.addAttribute("todayDate", todayDate);  // 오늘 날짜
//        model.addAttribute("todayScheduleList", todayScheduleList);  // 오늘 일정 리스트

        // 메모 관련
        // 서비스에서 전체 글 목록을 전달받음
        Page<MemoDTO> memoPage = memoService.getList(memberId, page, pageSize);

        // 글 목록을 모델에 저장
        model.addAttribute("memoPage", memoPage);
        model.addAttribute("page", page);

        // 쪽지 관련
        // 로그인한 사용자의 수신 쪽지 목록을 조회
        List<MessageDTO> receivedMessage = messageService.getreceivedListAll(user.getMemberId());
        // 모델에 수신 쪽지 목록을 추가하여 뷰에 전달
        model.addAttribute("receivedMessageList", receivedMessage);

        // 비콘
        if (memberId == null) {
            return "redirect:/login";
        }

        // 시간 포맷터 정의
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // 오늘의 출근 기록을 가져옴
        AttendanceEntity attendance = attendanceService.getTodayAttendance(memberId);
        model.addAttribute("attendance", attendance);

        // 버튼 상태 설정
        boolean isCheckInDisabled = false;
        boolean isCheckOutDisabled = true;
        String checkInTime = "00:00";
        String checkOutTime = "00:00";

        if (attendance != null) {
            if (attendance.getCheckIn() != null) {
                isCheckInDisabled = true;
                checkInTime = attendance.getCheckIn().toLocalTime().format(timeFormatter);;
                isCheckOutDisabled = false;
            }
            if (attendance.getCheckOut() != null) {
                isCheckOutDisabled = true;
                checkOutTime = attendance.getCheckOut().toLocalTime().format(timeFormatter);;
            }
        }

        model.addAttribute("isCheckInDisabled", isCheckInDisabled);
        model.addAttribute("isCheckOutDisabled", isCheckOutDisabled);
        model.addAttribute("checkInTime", checkInTime);
        model.addAttribute("checkOutTime", checkOutTime);

        return "main/main";
    }

    @GetMapping("roleregister")
    public String roleregister() {
        return "main/roleregister";
    }

    // 세션 값 출력 메서드
    private void printSessionAttributes(HttpSession session) {
        Enumeration<String> attributeNames = session.getAttributeNames();

        // 모든 세션 값을 로그로 출력
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            Object attributeValue = session.getAttribute(attributeName);

            log.debug("세션에 들어있는 값: {} = {}", attributeName, attributeValue);  // 로그로 출력
        }
    }

}
