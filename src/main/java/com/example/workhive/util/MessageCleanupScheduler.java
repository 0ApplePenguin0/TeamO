package com.example.workhive.util;

import com.example.workhive.service.MessageService;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageCleanupScheduler {

    private final MessageService messageService;

    // 매일 자정에 30일이 지난 메시지를 삭제하는 스케줄러
    @Scheduled(cron = "0 0 0 1/15 * ?")
    public void deleteOldMessages() {
        messageService.deleteOldMessages();
        System.out.println("30일 이상된 메시지를 삭제했습니다.");
    }
}