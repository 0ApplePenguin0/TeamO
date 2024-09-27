package com.example.workhive.controller;

import com.example.workhive.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherRestController {

    @Autowired
    private WeatherService weatherService;

    // 위도와 경도를 받아서 날씨 정보를 반환하는 엔드포인트
    @GetMapping("/api/weather")
    public String getWeatherByCoordinates(@RequestParam("lat") double lat, @RequestParam("lon") double lon) {
        return weatherService.getWeatherByCoordinates(lat, lon);
    }
}
