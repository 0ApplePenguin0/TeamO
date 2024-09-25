package com.example.workhive.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvitationCodeDTO {
    private Long codeId;
    private Long companyId;
    private String code;
    private LocalDateTime expirationDate;
    private Integer usageLimit;
    private Integer usageCount;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private String createdBy;
}
