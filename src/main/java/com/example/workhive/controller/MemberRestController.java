package com.example.workhive.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.workhive.domain.dto.MemberDTO;
import com.example.workhive.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberRestController {
	
	private final MemberService service;
	  @GetMapping("getmembers")
	    public List<MemberDTO> getAllMembers() {
	        return service.getAllMembers();
	    }
}
