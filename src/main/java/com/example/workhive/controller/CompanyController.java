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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@RequestMapping("/main/company")
public class CompanyController {

   private final CompanyService companyService;




   @GetMapping("positions")
   @ResponseBody
   public List<PositionDTO> getPositionsCompanyId(@RequestParam("companyId") Long companyId) {
      // 회사 URL로 직급 목록을 조회
      List<PositionEntity> positions = companyService.getPositionsByCompanyId(companyId);
      // 위에서 받아온 리스트를 스트림으로 변환
      return positions.stream()
              .map(posi -> PositionDTO.builder()
                      .positionId(posi.getPositionId())
                      .positionName(posi.getPositionName())
                      .build())
              //변환된 DepartmentDTO객체들이 리스트로 모아져 반환됨
              .collect(Collectors.toList());
   }

}