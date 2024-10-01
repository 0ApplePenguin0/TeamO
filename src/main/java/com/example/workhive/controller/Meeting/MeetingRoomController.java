package com.example.workhive.controller.Meeting;

import com.example.workhive.domain.entity.MeetingRoom.MeetingRoomEntity;
import com.example.workhive.domain.entity.MeetingRoom.MeetingRoomReservationEntity;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.repository.MemberRepository;
import com.example.workhive.repository.meeting.MeetingRoomRepository;
import com.example.workhive.security.AuthenticatedUser;
import com.example.workhive.service.meeting.MeetingRoomReservationService;
import com.example.workhive.service.meeting.MeetingRoomService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reserve/meetingRoom")
@RequiredArgsConstructor
public class MeetingRoomController {
    private final MeetingRoomService meetingRoomService;
    private final MeetingRoomReservationService reservationService;
    private final MemberRepository memberRepository;
    private final MeetingRoomRepository meetingRoomRepository;

    @GetMapping
    public String meetingRoomList(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
        String memberId = user.getMemberId();
        MemberEntity member = memberRepository.findByMemberId(memberId);
        if (member == null) {
            return "redirect:/login";
        }

        Long companyId = member.getCompany().getCompanyId();

        List<MeetingRoomEntity> meetingRooms = meetingRoomService.getMeetingRoomsByCompanyId(companyId);
        model.addAttribute("meetingRooms", meetingRooms);
        model.addAttribute("role", member.getRole());

        return "meet/reservation/meetingRoomList";
    }

    @GetMapping("/{roomId}")
    public String meetingRoomDetail(@PathVariable Long roomId, Model model) {
        MeetingRoomEntity meetingRoom = meetingRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Meeting room not found"));

        // 현재 날짜의 예약 정보 가져오기
        LocalDate today = LocalDate.now();
        List<MeetingRoomReservationEntity> reservations = reservationService.getReservationsByRoomIdAndDate(roomId, today);

        // 예약된 시간대 추출
        List<Integer> reservedHours = reservations.stream()
                .map(reservation -> reservation.getStartTime().getHour())
                .collect(Collectors.toList());

        model.addAttribute("meetingRoom", meetingRoom);
        model.addAttribute("reservations", reservations);
        model.addAttribute("reservedHours", reservedHours);

        return "meet/reservation/meetingRoomDetail";
    }

    @PostMapping("/{roomId}/reserve")
    public String reserveMeetingRoom(@PathVariable Long roomId,
                                     @RequestParam("startTime") String startTimeStr,
                                     @RequestParam("endTime") String endTimeStr,
                                     RedirectAttributes redirectAttributes,
                                     @AuthenticationPrincipal AuthenticatedUser user) {
        String memberId = user.getMemberId();
        if (memberId == null) {
            return "redirect:/login";
        }

        LocalDateTime startTime = LocalDateTime.parse(startTimeStr);
        LocalDateTime endTime = LocalDateTime.parse(endTimeStr);

        // 시간 유효성 검사
        if (!startTime.isBefore(endTime)) {
            redirectAttributes.addFlashAttribute("error", "종료 시간은 시작 시간보다 이후여야 합니다.");
            return "redirect:/reserve/meetingRoom/" + roomId;
        }

        boolean success = reservationService.reserveMeetingRoom(roomId, memberId, startTime, endTime);

        if (success) {
            redirectAttributes.addFlashAttribute("message", "예약이 완료되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("error", "해당 시간에는 이미 예약이 있습니다.");
        }

        return "redirect:/reserve/meetingRoom/" + roomId;
    }

    @GetMapping("/add")
    public String addMeetingRoomForm(Model model) {
        model.addAttribute("meetingRoom", new MeetingRoomEntity());
        return "meet/reservation/addMeetingRoom";
    }

    @PostMapping("/add")
    public String addMeetingRoom(@ModelAttribute MeetingRoomEntity meetingRoom, RedirectAttributes redirectAttributes,
                                 @AuthenticationPrincipal AuthenticatedUser user) {
        String memberId = user.getMemberId();
        if (memberId == null) {
            return "redirect:/login";
        }

        MemberEntity member = memberRepository.findByMemberId(memberId);


        if (member.getRole() != MemberEntity.RoleEnum.ROLE_ADMIN) {
            redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
            return "redirect:/reserve/meetingRoom";
        }

        meetingRoomService.addMeetingRoom(member.getCompany().getCompanyId(),
                meetingRoom.getRoomName(), meetingRoom.getLocation(), meetingRoom.getCapacity());

        redirectAttributes.addFlashAttribute("message", "회의실이 추가되었습니다.");
        return "redirect:/reserve/meetingRoom";
    }
}