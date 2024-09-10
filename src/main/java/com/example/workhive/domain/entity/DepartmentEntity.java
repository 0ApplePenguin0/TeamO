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
@Table(name = "departments")
public class DepartmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_num")
    private int departmentNum;

    @ManyToOne
    @JoinColumn(name = "company_url", referencedColumnName = "company_url", nullable = false)
    private CompanyEntity company;

    @Column(name = "department_name", nullable = false, length = 100, columnDefinition = "varchar(100) default '발령 대기'")
    private String departmentName;

}

