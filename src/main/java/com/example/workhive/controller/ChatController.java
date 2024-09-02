package com.example.workhive.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("chat")
@RequiredArgsConstructor
public class ChatController {

    @GetMapping("mainChatPage")
    public String mainChatPage(){
        return "chat/mainChatPage";
    };
    
    @GetMapping("createPage")
    public String createPage(){
        return "chat/createPage";
    };
}
