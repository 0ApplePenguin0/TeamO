package com.example.workhive.service.AttendanceService;

import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.domain.entity.attendance.AttendanceEntity;
import com.example.workhive.repository.AttendanceRepository;
import com.example.workhive.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalDateTime;


import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final MemberRepository memberRepository;

    // 출근 처리
    public AttendanceEntity checkIn(String memberId) {
        MemberEntity member = memberRepository.findByMemberId(memberId);
        if (member == null) {
            throw new RuntimeException("회원 정보를 찾을 수 없습니다.");
        }

        // 이미 오늘 출근했는지 확인
        LocalDate today = LocalDate.now();
        Optional<AttendanceEntity> optionalAttendance = attendanceRepository.findByMemberAndAttendanceDate(member, today);

        if (optionalAttendance.isPresent()) {
            throw new RuntimeException("이미 출근 처리가 되었습니다.");
        }

        AttendanceEntity attendance = AttendanceEntity.builder()
                .member(member)
                .attendanceDate(today)
                .checkIn(LocalDateTime.now())
                .status("출근")
                .build();

        return attendanceRepository.save(attendance);
    }

    // 퇴근 처리
    public AttendanceEntity checkOut(String memberId) {
        MemberEntity member = memberRepository.findByMemberId(memberId);
        if (member == null) {
            throw new RuntimeException("회원 정보를 찾을 수 없습니다.");
        }

        // 오늘 출근 기록을 찾음
        LocalDate today = LocalDate.now();
        AttendanceEntity attendance = attendanceRepository.findByMemberAndAttendanceDate(member, today)
                .orElseThrow(() -> new RuntimeException("출근 기록이 없습니다."));

        if (attendance.getCheckOut() != null) {
            throw new RuntimeException("이미 퇴근 처리가 되었습니다.");
        }

        attendance.setCheckOut(LocalDateTime.now());
        attendance.setStatus("퇴근");

        return attendanceRepository.save(attendance);
    }

    // 출근 상태 확인
    public AttendanceEntity getTodayAttendance(String memberId) {
        MemberEntity member = memberRepository.findByMemberId(memberId);
        if (member == null) {
            throw new RuntimeException("회원 정보를 찾을 수 없습니다.");
        }
        LocalDate today = LocalDate.now();
        return attendanceRepository.findByMemberAndAttendanceDate(member, today).orElse(null);
    }

    // 주소 정제 메서드
    public String cleanAddress(String address) {
        if (address == null || address.isEmpty()) {
            return address;
        }

        // 우편번호 제거
        address = address.replaceAll("^\\d{5}\\s*", "");

        // 괄호 안의 내용 제거
        address = address.replaceAll("\\s*\\(.*\\)$", "");

        // 건물 번호 및 추가 정보 제거
        address = address.replaceAll("\\s+\\d+\\s*$", "");

        return address.trim();
    }

    // 회사 주소 가져오기
    public String getCompanyAddress(String memberId) {
        MemberEntity member = memberRepository.findByMemberId(memberId);
        if (member == null || member.getCompany() == null) {
            throw new RuntimeException("회원 또는 회사 정보를 찾을 수 없습니다.");
        }
        return member.getCompany().getCompanyAddress();
    }
}