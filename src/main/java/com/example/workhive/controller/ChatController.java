package com.example.workhive.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.workhive.domain.entity.ChatRoomEntity;
import com.example.workhive.service.ChatRoomService;

import ch.qos.logback.core.model.Model;

@Controller
@RequestMapping("chat")
@RequiredArgsConstructor
public class ChatController {

	private final ChatRoomService chatRoomService;
	
    @GetMapping("mainChatPage")
    public String mainChatPage(){
        return "chat/mainChatPage";
    };
    
    @GetMapping("createPage")
    public String createPage(){
        return "chat/createPage";
    };
    
}
