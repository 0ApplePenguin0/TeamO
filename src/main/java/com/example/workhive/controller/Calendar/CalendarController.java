package com.example.workhive.controller.Calendar;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("calendar" )
@RequiredArgsConstructor
public class CalendarController {

    @GetMapping("viewCalendar")
    public String viewCalendar(){
        return "main/calendar/calendarFage";
    }
    //viewCalendar
}
