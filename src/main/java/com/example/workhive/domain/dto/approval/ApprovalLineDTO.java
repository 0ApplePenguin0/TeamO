package com.example.workhive.domain.dto.approval;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalLineDTO {
    private Long approvalLineId;
    private Long departmentId;
    private String departmentName;
    private Long teamId;
    private String teamName;
    private String memberId;
    private String memberName;
    private Integer stepOrder;
    private String status;
    private String comment;
    private LocalDateTime approvalDate;
}