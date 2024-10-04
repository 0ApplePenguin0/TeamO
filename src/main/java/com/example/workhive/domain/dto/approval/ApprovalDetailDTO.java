package com.example.workhive.domain.dto.approval;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalDetailDTO {
    private Long approvalId;
    private String formName;
    private String content;
    private String approvalStatus;
    private LocalDateTime requestDate;
    private List<ApprovalLineDTO> approvalLines;
}