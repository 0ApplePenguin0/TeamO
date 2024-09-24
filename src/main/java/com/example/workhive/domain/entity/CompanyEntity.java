package com.example.workhive.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 회사 정보 Entity
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "company")
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id", nullable = false)
    private Long companyId;  // company_id를 Long 타입으로 변경

    @Column(name = "company_name", nullable = false, length = 255)
    private String companyName;

    @Column(name = "company_address", nullable = false, length = 255)
    private String companyAddress;

    @Column(name ="company_url", length = 255)
    private String companyUrl;
}
