package com.example.workhive.controller.Meeting;

import com.example.workhive.domain.dto.meet.MeetingRoomDetailDTO;
import com.example.workhive.domain.dto.meet.ReservationDTO;
import com.example.workhive.domain.entity.MeetingRoom.RoomStatus;
import com.example.workhive.domain.dto.meet.MeetingRoomDTO;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.repository.MemberRepository;
import com.example.workhive.security.AuthenticatedUser;
import com.example.workhive.service.meeting.MeetingRoomReservationService;
import com.example.workhive.service.meeting.MeetingRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/reserve/meetingRoom")
@RequiredArgsConstructor
@Slf4j
public class MeetingRoomController {
    private final MeetingRoomService meetingRoomService;
    private final MeetingRoomReservationService reservationService;
    private final MemberRepository memberRepository;

    // 회의실 목록
    @GetMapping
    public String meetingRoomList(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
        String memberId = user.getMemberId();
        MemberEntity member = memberRepository.findByMemberId(memberId);
        if (member == null) {
            return "redirect:/login";
        }

        Long companyId = member.getCompany().getCompanyId();

        List<MeetingRoomDTO> meetingRooms = meetingRoomService.getMeetingRoomsByCompanyId(companyId);
        log.info("잘 들어오나 {} meeting rooms company {}", meetingRooms.size(), companyId);
        model.addAttribute("meetingRooms", meetingRooms);
        model.addAttribute("role", member.getRole().name()); // 역할을 문자열로 추가

        return "meet/reservation/meetingRoomList";
    }

    // 회의실 상세 보기 및 예약 정보
    @GetMapping("/{roomId}")
    public String meetingRoomDetail(@PathVariable Long roomId, Model model, @AuthenticationPrincipal AuthenticatedUser user) {
        MeetingRoomDetailDTO meetingRoomDetail = meetingRoomService.getMeetingRoomDetail(roomId);
        model.addAttribute("meetingRoom", meetingRoomDetail);

        // 회의실 상태가 UNAVAILABLE인 경우 에러 메시지 추가
        if ("UNAVAILABLE".equals(meetingRoomDetail.getRoomStatus())) {
            model.addAttribute("error", "이 회의실은 현재 예약할 수 없습니다.");
        }

        // 예약 현황에서 CONFIRMED 상태인 예약만 추출하여 sortedReservations 리스트 생성
        List<ReservationDTO> sortedReservations = meetingRoomDetail.getReservations().stream()
                .filter(reservation -> "CONFIRMED".equals(reservation.getStatus())) // only confirmed
                .sorted(Comparator.comparing(ReservationDTO::getStartTime)) // sort by startTime
                .collect(Collectors.toList());

        model.addAttribute("reservations", sortedReservations);

        // 예약된 시간대 계산
        Set<Integer> reservedHours = sortedReservations.stream()
                .flatMap(reservation -> {
                    LocalDateTime start = reservation.getStartTime();
                    LocalDateTime end = reservation.getEndTime();
                    if (start == null || end == null) {
                        return IntStream.empty().boxed();
                    }
                    return IntStream.range(start.getHour(), end.getHour()).boxed();
                })
                .collect(Collectors.toSet());

        model.addAttribute("reservedHours", reservedHours);

        // 예약 가능한 시간 리스트 생성
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        List<String> availableStartTimes = IntStream.rangeClosed(9, 17)
                .mapToObj(hour -> now.withHour(hour).withMinute(0).withSecond(0).withNano(0).format(formatter))
                .collect(Collectors.toList());
        model.addAttribute("availableStartTimes", availableStartTimes);

        List<String> availableEndTimes = IntStream.rangeClosed(10, 18)
                .mapToObj(hour -> now.withHour(hour).withMinute(0).withSecond(0).withNano(0).format(formatter))
                .collect(Collectors.toList());
        model.addAttribute("availableEndTimes", availableEndTimes);

        // 사용자 정보 추가 (role 포함)
        String memberId = user.getMemberId();
        MemberEntity member = memberRepository.findByMemberId(memberId);
        model.addAttribute("role", member.getRole().name()); // 역할을 문자열로 추가
        model.addAttribute("memberId", memberId);

        return "meet/reservation/meetingRoomDetail";
    }

    // 회의실 예약
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

        LocalDateTime startTime;
        LocalDateTime endTime;
        try {
            startTime = LocalDateTime.parse(startTimeStr);
            endTime = LocalDateTime.parse(endTimeStr);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "잘못된 시간 형식입니다.");
            return "redirect:/reserve/meetingRoom/" + roomId;
        }

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

    // 회의실 추가 폼 (ROLE_ADMIN)
    @GetMapping("/add")
    public String addMeetingRoomForm(Model model, @AuthenticationPrincipal AuthenticatedUser user, RedirectAttributes redirectAttributes) {
        String memberId = user.getMemberId();
        MemberEntity member = memberRepository.findByMemberId(memberId);
        if (member == null || !MemberEntity.RoleEnum.ROLE_ADMIN.equals(member.getRole())) {
            redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
            return "redirect:/reserve/meetingRoom";
        }

        model.addAttribute("meetingRoom", new MeetingRoomDTO());
        model.addAttribute("roomStatuses", RoomStatus.values()); // RoomStatus 값을 모델에 추가
        return "meet/reservation/addMeetingRoom";
    }

    // 회의실 추가 처리 (ROLE_ADMIN)
    @PostMapping("/add")
    public String addMeetingRoom(@ModelAttribute MeetingRoomDTO meetingRoomDTO, RedirectAttributes redirectAttributes,
                                 @AuthenticationPrincipal AuthenticatedUser user) {
        String memberId = user.getMemberId();
        if (memberId == null) {
            return "redirect:/login";
        }

        MemberEntity member = memberRepository.findByMemberId(memberId);

        if (!MemberEntity.RoleEnum.ROLE_ADMIN.equals(member.getRole())) {
            redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
            return "redirect:/reserve/meetingRoom";
        }

        // 회의실 추가
        meetingRoomService.addMeetingRoom(member.getCompany().getCompanyId(),
                meetingRoomDTO.getRoomName(), meetingRoomDTO.getLocation(), meetingRoomDTO.getCapacity());

        redirectAttributes.addFlashAttribute("message", "회의실이 추가되었습니다.");
        return "redirect:/reserve/meetingRoom";
    }

    // 회의실 수정 폼 (ROLE_ADMIN만 가능)
    @GetMapping("/{roomId}/edit")
    public String editMeetingRoomForm(@PathVariable Long roomId, Model model, @AuthenticationPrincipal AuthenticatedUser user, RedirectAttributes redirectAttributes) {
        // 회의실 상세 정보 가져오기
        MeetingRoomDetailDTO meetingRoomDetail = meetingRoomService.getMeetingRoomDetail(roomId);
        model.addAttribute("meetingRoom", meetingRoomDetail);
        model.addAttribute("roomStatuses", RoomStatus.values());

        // ROLE_ADMIN 확인
        MemberEntity member = memberRepository.findByMemberId(user.getMemberId());
        if (member == null || !MemberEntity.RoleEnum.ROLE_ADMIN.equals(member.getRole())) {
            redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
            return "redirect:/reserve/meetingRoom";
        }

        return "meet/reservation/editMeetingRoom";
    }

    // 회의실 수정 처리 (ROLE_ADMIN만 가능)
    @PostMapping("/{roomId}/edit")
    public String editMeetingRoom(@PathVariable Long roomId,
                                  @ModelAttribute MeetingRoomDTO meetingRoomDTO,
                                  RedirectAttributes redirectAttributes,
                                  @AuthenticationPrincipal AuthenticatedUser user) {
        // 현재 사용자 확인 및 ROLE_ADMIN 확인
        MemberEntity member = memberRepository.findByMemberId(user.getMemberId());
        if (member == null || !MemberEntity.RoleEnum.ROLE_ADMIN.equals(member.getRole())) {
            redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
            return "redirect:/reserve/meetingRoom";
        }

        // roomStatus를 String에서 RoomStatus enum으로 변환
        RoomStatus status;
        try {
            status = RoomStatus.valueOf(meetingRoomDTO.getRoomStatus());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "잘못된 회의실 상태입니다.");
            return "redirect:/reserve/meetingRoom/" + roomId + "/edit";
        }

        // 회의실 정보 수정 처리
        meetingRoomService.updateMeetingRoom(roomId, meetingRoomDTO.getRoomName(),
                meetingRoomDTO.getLocation(), meetingRoomDTO.getCapacity(), status);

        redirectAttributes.addFlashAttribute("message", "회의실 정보가 수정되었습니다.");
        return "redirect:/reserve/meetingRoom";
    }

    // 회의실 삭제 (ROLE_ADMIN)
    @PostMapping("/{roomId}/delete")
    public String deleteMeetingRoom(@PathVariable Long roomId, RedirectAttributes redirectAttributes,
                                    @AuthenticationPrincipal AuthenticatedUser user) {
        String memberId = user.getMemberId();
        MemberEntity member = memberRepository.findByMemberId(memberId);

        if (member == null || !MemberEntity.RoleEnum.ROLE_ADMIN.equals(member.getRole())) {
            redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
            return "redirect:/reserve/meetingRoom";
        }

        meetingRoomService.deleteMeetingRoom(roomId);
        redirectAttributes.addFlashAttribute("message", "회의실이 삭제되었습니다.");
        return "redirect:/reserve/meetingRoom";
    }

    // 예약 취소 (자신의 예약만 취소 가능)
    @PostMapping("/{roomId}/reservation/{reservationId}/cancel")
    public String cancelReservation(@PathVariable Long roomId, @PathVariable Long reservationId,
                                    RedirectAttributes redirectAttributes, @AuthenticationPrincipal AuthenticatedUser user) {
        String memberId = user.getMemberId();
        if (memberId == null) {
            return "redirect:/login";
        }

        // 서비스 메서드가 List<Integer>를 반환하는 경우
        List<Integer> canceledHours = reservationService.cancelReservationByUser(reservationId, memberId);

        if (canceledHours != null && !canceledHours.isEmpty()) {
            // 성공적으로 취소되었을 경우 메시지 설정
            redirectAttributes.addFlashAttribute("message", "예약이 취소되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("error", "예약 취소에 실패하였습니다.");
        }

        // 예약 취소 후 해당 회의실의 업데이트된 예약 정보를 다시 불러옴
        return "redirect:/reserve/meetingRoom/" + roomId;
    }
}
