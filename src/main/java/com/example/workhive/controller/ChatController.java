package com.example.workhive.controller;

import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("teamChatPage")
    public String teamChatPage(Model model) {
        return "chat/teamChatPage";
    }

    @GetMapping("projectChatPage/{chatRoomId}")
    public String projectChatPage(@PathVariable("chatRoomId") Long chatRoomId, Model model) {
        model.addAttribute("chatRoomId", chatRoomId);  // chatRoomId를 모델에 추가
        return "chat/projectChatPage";  // 프로젝트 채팅방 페이지 템플릿
    }
}

