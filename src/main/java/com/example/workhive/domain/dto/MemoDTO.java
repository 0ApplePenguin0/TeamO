package com.example.workhive.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoDTO {
    private Long memoId;           // 메모 ID
    private String memberId;        // 작성자 ID
    private String title;           // 메모 제목
    private String content;         // 메모 내용
    private LocalDateTime createdAt; // 작성 시간
}
