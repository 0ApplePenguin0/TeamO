package com.example.workhive.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("chat")
@RequiredArgsConstructor
public class ChatController {

    @GetMapping("chatroom")
    public String chatroom(){
        return "chat/chatroom";
    };
}
