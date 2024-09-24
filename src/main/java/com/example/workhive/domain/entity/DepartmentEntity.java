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
    @Column(name = "department_id")  // 테이블의 department_id와 일치
    private Long departmentId;  // 타입 변경: BIGINT에 맞게 Long으로 수정

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "company_id", nullable = false)  // 외래키 변경
    private CompanyEntity company;

    @Column(name = "department_name", nullable = false, length = 50)  // 길이 변경
    private String departmentName;
}
