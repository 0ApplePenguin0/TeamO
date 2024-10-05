package com.example.workhive.domain.entity.Approval;

import com.example.workhive.domain.entity.CompanyEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "form_template")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormTemplateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id")
    private Long templateId;

    @Column(name = "form_name", length = 100, nullable = false)
    private String formName;

    @Column(name = "form_structure", columnDefinition = "JSON", nullable = false)
    private String formStructure;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}