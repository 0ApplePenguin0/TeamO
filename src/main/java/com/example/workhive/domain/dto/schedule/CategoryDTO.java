package com.example.workhive.domain.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {

    private Long categoryId;  // 카테고리 ID

    private String categoryName;  // 카테고리 이름

    private String color;  // 카테고리 색상
}
