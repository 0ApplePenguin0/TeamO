package com.example.workhive.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@PropertySource("classpath:config.properties")
public class WeatherRestController {

    @Value("${weather.api.key}")
    private String apikey;

    @GetMapping("/api/weather/key")
    public String getWeatherApiKey() {
        return apikey;
    }
}
