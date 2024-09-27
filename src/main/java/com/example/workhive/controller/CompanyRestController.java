package com.example.workhive.controller;

import com.example.workhive.repository.CompanyRepository;
import com.example.workhive.service.AttendanceService.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/company")
public class CompanyRestController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private CompanyRepository companyRepository; // 회사 정보를 가져올 리포지토리

    // 회사 좌표 반환 API
    @GetMapping("/location")
    public Map<String, Double> getCompanyLocation() {
        // 데이터베이스에서 회사 주소를 가져옴
        Company company = companyRepository.findById(1L).orElseThrow(() -> new RuntimeException("회사 정보가 없습니다."));
        String companyAddress = company.getAddress();

        double[] coordinates = locationService.getCoordinatesFromAddress(companyAddress);

        Map<String, Double> location = new HashMap<>();
        location.put("latitude", coordinates[0]);
        location.put("longitude", coordinates[1]);

        return location;
    }