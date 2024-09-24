package com.example.workhive.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 직급 정보 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionDTO {

    private Long positionId;         // 직급 ID (Primary Key)
    private Long companyId;          // 회사 ID (외래키)
    private String positionName;     // 직급 이름
}
