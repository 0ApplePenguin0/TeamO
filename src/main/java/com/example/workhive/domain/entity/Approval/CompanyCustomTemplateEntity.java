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
public class CompanyCustomTemplateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custom_template_id")
    private Long customTemplateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @ToString.Exclude
    private CompanyEntity company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    @ToString.Exclude
    private FormTemplateEntity formTemplate;

    @Column(name = "custom_structure", columnDefinition = "JSON", nullable = false)
    private String customStructure;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}