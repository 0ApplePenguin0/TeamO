package com.example.workhive.controller;

import com.example.workhive.domain.dto.MemberDetailDTO;
import com.example.workhive.domain.dto.PositionDTO;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.domain.entity.PositionEntity;
import com.example.workhive.repository.CompanyRepository;
import com.example.workhive.repository.MemberRepository;
import com.example.workhive.security.AuthenticatedUser;
import com.example.workhive.service.CompanyService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@Controller
@RequestMapping("/main/company")
public class CompanyController {

   private final CompanyService companyService;




}