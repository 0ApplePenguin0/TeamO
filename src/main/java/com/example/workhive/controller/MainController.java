package com.example.workhive.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("main")
@RequiredArgsConstructor
public class MainController {

    @GetMapping("board")
    public String board(){
        return "main/mainboard";
    };
}
