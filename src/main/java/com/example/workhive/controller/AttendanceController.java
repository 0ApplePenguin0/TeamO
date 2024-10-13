package com.example.workhive.controller;

import com.example.workhive.domain.dto.attendance.AttendanceDTO;
import com.example.workhive.domain.entity.attendance.AttendanceEntity;
import com.example.workhive.service.AttendanceService.AttendanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Controller
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final ObjectMapper objectMapper;


    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    private final AttendanceService attendanceService;
    private final HttpSession session;

    @GetMapping
    public String attendancePage(Model model) {
        String memberId = (String) session.getAttribute("memberId");
        if (memberId == null) {
            return "redirect:/login";
        }

        // 오늘의 출근 기록을 가져옴
        AttendanceEntity attendance = attendanceService.getTodayAttendance(memberId);
        model.addAttribute("attendance", attendance);

        // 버튼 상태 설정
        boolean isCheckInDisabled = false;
        boolean isCheckOutDisabled = true;
        String checkInTime = "--:--";
        String checkOutTime = "--:--";

        if (attendance != null) {
            if (attendance.getCheckIn() != null) {
                isCheckInDisabled = true;
                checkInTime = attendance.getCheckIn().toLocalTime().toString();
                isCheckOutDisabled = false;
            }
            if (attendance.getCheckOut() != null) {
                isCheckOutDisabled = true;
                checkOutTime = attendance.getCheckOut().toLocalTime().toString();
            }
        }

        model.addAttribute("isCheckInDisabled", isCheckInDisabled);
        model.addAttribute("isCheckOutDisabled", isCheckOutDisabled);
        model.addAttribute("checkInTime", checkInTime);
        model.addAttribute("checkOutTime", checkOutTime);

        return "main/board/attendance";
    }

    @Data
    static class testDTO {
        Double userLatitude;
        Double userLongitude;
    }

    @PostMapping("check-in")
    @ResponseBody
    public Map<String, Object> checkIn(@RequestBody Map<String, Double> location) {
        String memberId = (String) session.getAttribute("memberId");
        if (memberId == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        // 위치 비교 로직
        boolean isWithinRange = compareLocation(memberId, location);
        Map<String, Object> response = new HashMap<>();

        if (isWithinRange) {
            AttendanceEntity attendance = attendanceService.checkIn(memberId);
            response.put("status", "success");
            response.put("checkInTime", attendance.getCheckIn().toString());
        } else {
            response.put("status", "fail");
            response.put("message", "출근 범위 내에 있지 않습니다.");
        }

        return response;
    }

//    @PostMapping("/check-in")
//    @ResponseBody
//    public String checkIn(@ModelAttribute testDTO location) {
//        log.debug("로케이션 {}", location);
//
//
//        String memberId = (String) session.getAttribute("memberId");
//        if (memberId == null) {
//            throw new RuntimeException("로그인이 필요합니다.");
//        }
//
//
//        return "test";
//        // 위치 비교 로직
//        boolean isWithinRange = compareLocation(memberId, location);
//        Map<String, Object> response = new HashMap<>();
//
//        if (isWithinRange) {
//            AttendanceEntity attendance = attendanceService.checkIn(memberId);
//            response.put("status", "success");
//            response.put("checkInTime", attendance.getCheckIn().toString());
//        } else {
//            response.put("status", "fail");
//            response.put("message", "출근 범위 내에 있지 않습니다.");
//        }

//        return response;
//    }

    @PostMapping("/check-out")
    @ResponseBody
    public Map<String, Object> checkOut(@RequestBody Map<String, Double> location) {
        String memberId = (String) session.getAttribute("memberId");
        if (memberId == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        // 위치 비교 로직
        boolean isWithinRange = compareLocation(memberId, location);
        Map<String, Object> response = new HashMap<>();

        if (isWithinRange) {
            AttendanceEntity attendance = attendanceService.checkOut(memberId);
            response.put("status", "success");
            response.put("checkOutTime", attendance.getCheckOut().toString());
        } else {
            response.put("status", "fail");
            response.put("message", "퇴근 범위 내에 있지 않습니다.");
        }

        return response;
    }

    // 위치 비교 로직
    private boolean compareLocation(String memberId, Map<String, Double> userLocation) {
        // 회사 위치 가져오기
        String companyAddress = attendanceService.getCompanyAddress(memberId);

        // 회사 주소를 좌표로 변환 (Kakao Map API 사용)
        Map<String, Double> companyCoordinates = getCoordinatesFromAddress(companyAddress);

        // 거리 계산
        double distance = calculateDistance(
                userLocation.get("latitude"), userLocation.get("longitude"),
                companyCoordinates.get("latitude"), companyCoordinates.get("longitude")
        );

        // 일정 거리(distance) 이내인지 확인
        return distance <= 10000;
    }

    // 주소를 좌표로 변환하는 메서드 (Kakao Map API 사용)
    private Map<String, Double> getCoordinatesFromAddress(String address) {
        try {
            // 주소 정제
            String cleanedAddress = attendanceService.cleanAddress(address);

            String apiUrl = "https://dapi.kakao.com/v2/local/search/address.json"
                    + "?analyze_type=similar&page=1&size=10&query=" + cleanedAddress;

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoRestApiKey);
            headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
            headers.set("User-Agent", "Mozilla/5.0");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);


            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject jsonObject = new JSONObject(response.getBody());
                JSONArray documents = jsonObject.getJSONArray("documents");
                if (documents.length() > 0) {
                    JSONObject location = documents.getJSONObject(0);
                    double longitude = Double.parseDouble(location.getString("x"));
                    double latitude = Double.parseDouble(location.getString("y"));

                    Map<String, Double> coordinates = new HashMap<>();
                    coordinates.put("latitude", latitude);
                    coordinates.put("longitude", longitude);
                    return coordinates;
                } else {
                    throw new RuntimeException("주소의 좌표를 찾을 수 없습니다.");
                }
            } else {
                throw new RuntimeException("Kakao Map API 호출 실패: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("좌표 변환 중 오류 발생: " + e.getMessage(), e);
        }
    }

    // 두 좌표 사이의 거리를 계산하는 메서드 (단위: 미터)
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371000; // 지구 반지름 (미터)

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = EARTH_RADIUS * c;

        return distance; // 단위: 미터
    }

    @GetMapping("/monthly")
    public String getMonthlyAttendance(@RequestParam("year") int year,
                                       @RequestParam("month") int month,
                                       Model model) {
        String memberId = (String) session.getAttribute("memberId");
        if (memberId == null) {
            return "redirect:/login";
        }

        List<AttendanceDTO> monthlyAttendance = attendanceService.getMonthlyAttendance(memberId, year, month);
        model.addAttribute("monthlyAttendance", monthlyAttendance);
        model.addAttribute("year", year);
        model.addAttribute("month", month);

        try {
            String monthlyAttendanceJson = objectMapper.writeValueAsString(monthlyAttendance);
            model.addAttribute("monthlyAttendanceJson", monthlyAttendanceJson);
        } catch (Exception e) {
            // 예외 처리
            model.addAttribute("monthlyAttendanceJson", "[]");
        }

        return "main/board/attendanceGraph";
    }
}