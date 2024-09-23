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
@Table(name = "team")
public class TeamEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long teamId; // ID 타입을 Long으로 변경

    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "department_id", nullable = false) // department_id 외래키 설정
    private DepartmentEntity department;

    @Column(name = "team_name", length = 50, nullable = false)
    private String teamName; // 팀 이름

}

