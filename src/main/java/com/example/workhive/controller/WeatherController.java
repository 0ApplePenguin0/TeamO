package com.example.workhive.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WeatherController {

    // /weather로 요청 시 templates/main/board/weather.html 반환
    @GetMapping("/weather")
    public String weather() {
        return "main/board/weather";  // 뷰 템플릿 경로
    }
}
