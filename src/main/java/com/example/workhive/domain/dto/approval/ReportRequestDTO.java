package com.example.workhive.domain.dto.approval;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportRequestDTO {
    private String title;
    private String content;
//    private Map<String, Object> content = new HashMap<>();

    private Long templateId;
    private List<String> approvalLineMemberIds; // 결재자들의 ID 리스트

}
