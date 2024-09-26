package com.example.workhive.controller;

import com.example.workhive.service.WeatherService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherRestController {

    @Autowired
    private WeatherService weatherService;

    // 로그인한 유저의 회사 ID를 세션에서 받아와 날씨 정보를 가져옴
    @GetMapping("/api/weather/company")
    public String getWeatherByLoggedInUser(HttpSession session) {
        // 세션에서 로그인한 유저의 회사 ID 가져오기 (세션에 companyId가 저장되어 있다고 가정)
        Long companyId = (Long) session.getAttribute("companyId");

        if (companyId == null) {
            return "회사 ID를 찾을 수 없습니다. 로그인을 확인하세요.";
        }

        // WeatherService를 통해 날씨 정보 가져오기
        return weatherService.getWeatherByCompanyId(companyId);
    }
}