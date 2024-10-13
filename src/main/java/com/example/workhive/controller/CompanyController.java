package com.example.workhive.controller;

import com.example.workhive.domain.dto.PositionDTO;
import com.example.workhive.domain.entity.PositionEntity;
import com.example.workhive.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
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