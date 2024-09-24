package com.example.workhive.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 팀 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamDTO {

    private Long teamId;            // 팀 ID
    private Long departmentId;       // 부서 ID (외래키)
    private String teamName;         // 팀 이름
}
