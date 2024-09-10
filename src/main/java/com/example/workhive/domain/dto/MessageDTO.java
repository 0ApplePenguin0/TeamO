package com.example.workhive.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private int messageNum;
    private String senderUserId;
    private String receiverUserId;
    private String companyUrl;
    private String title;
    private String content;
    private LocalDateTime sentTime;
    private boolean readChk;
    private boolean deleteStatus;
    private LocalDateTime deleteDate;
    private String originalName;
    private String fileName;
    private int departmentNum;
    private int subdepNum;
}
