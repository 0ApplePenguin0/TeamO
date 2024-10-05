package com.example.workhive.domain.entity.Approval;

import com.example.workhive.domain.entity.CompanyEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "company_custom_template")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(CompanyCustomTemplateId.class)
public class CompanyCustomTemplateEntity {

    @Id
    @Column(name = "company_id")
    private Long companyId;

    @Id
    @Column(name = "template_id")
    private Long templateId;

    @Column(name = "custom_structure", nullable = false, columnDefinition = "JSON")
    private String customStructure;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    private CompanyEntity company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", insertable = false, updatable = false)
    private FormTemplateEntity formTemplate;
}