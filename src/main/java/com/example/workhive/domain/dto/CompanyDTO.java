package com.example.workhive.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {
    private Long companyId;        // BIGINT, auto-increment primary key
    private String companyName;    // VARCHAR(255) NOT NULL
    private String companyAddress; // VARCHAR(255) NOT NULL
}
