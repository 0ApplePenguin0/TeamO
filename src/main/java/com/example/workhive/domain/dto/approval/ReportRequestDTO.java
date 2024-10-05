package com.example.workhive.domain.dto.approval;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportRequestDTO {
    private String title;
    private String content;
    private Long templateId;
    private List<ApprovalLineDTO> approvalLines;
    private List<Long> approvalLineMemberIds; // 결재자들의 ID 리스트

}
