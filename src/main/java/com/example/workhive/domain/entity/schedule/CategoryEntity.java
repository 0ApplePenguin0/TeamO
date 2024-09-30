package com.example.workhive.domain.entity.schedule;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "category")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;  // 카테고리 ID (일련번호)

    @Column(name = "category_name", length = 50)
    private String categoryName;  // 카테고리 이름 (null 가능)

    @Column(name = "color", nullable = false, length = 255)
    private String color;  // 카테고리 색상 (null 불가)

}
