package com.example.workhive.domain.dto.approval;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalRequestDTO {
    private Long templateId;
    private String content;
    private List<ApprovalLineDTO> approvalLines;
}