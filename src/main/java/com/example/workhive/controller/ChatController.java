package com.example.workhive.controller;

import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import org.springframework.ui.Model;

@Controller
@RequestMapping("chat")
@RequiredArgsConstructor
public class ChatController {

    @GetMapping("mainChatPage")
    public String mainChatPage(Model model) {
        return "chat/mainChatPage";
    }

    @GetMapping("createPage")
    public String createPage() {
        return "chat/createPage";
    }
}
