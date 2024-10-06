package com.example.workhive.domain.dto.approval;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalDetailDTO {
    private Long approvalId;
    private String formName;
    private String title;
    private Map<String, Object> content;
   // private String content;
    private String approvalStatus;
    private LocalDateTime requestDate;
    private List<ApprovalLineDTO> approvalLines;
    private String requesterName;
    private String formStructure;

    public String getApprovalStatusInKorean() {
        switch (approvalStatus) {
            case "PENDING":
                return "보류";
            case "APPROVED":
                return "승인";
            case "REJECTED":
                return "반려";
            default:
                return approvalStatus;
        }
    }

    // FormField 클래스는 양식 필드의 구조를 정의
    @Data
    public static class FormField {
        private String name;
        private String type;
        private String label;
        private boolean required;
        private List<Value> values; // 선택형 필드인 경우

        @Data
        public static class Value {
            private String label;
            private String value;
        }
    }
}