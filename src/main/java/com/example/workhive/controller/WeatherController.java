package com.example.workhive.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@Controller
public class WeatherController {

    @GetMapping("/weather")
    public String getWeather() {
        return "main/board/weather";
    }
    }
