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
@Table(name = "subdeps")
public class SubDepartmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subdep_num")
    private int subdepNum;

    @ManyToOne
    @JoinColumn(name = "company_url", referencedColumnName = "company_url", nullable = false)
    private CompanyEntity company;

    @ManyToOne
    @JoinColumn(name = "department_num", referencedColumnName = "department_num", nullable = false)
    private DepartmentEntity department;

    @Column(name = "subdep_name", length = 100)
    private String subdepName;

}

