package com.example.workhive.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "department")
public class DepartmentEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")  // department_id와 일치
    private Long departmentId;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)  // company_id와 일치
    private CompanyEntity company;

    @Column(name = "department_name", nullable = false, length = 50)
    private String departmentName;
}
