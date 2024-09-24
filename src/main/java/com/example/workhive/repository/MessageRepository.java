package com.example.workhive.repository;

import com.example.workhive.domain.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    // 메시지 번호로 메시지 찾기
    MessageEntity findByMessageId(Long messageId);

    // 삭제 전에 메시지를 찾는 메서드는 필요 없음. (deleteDate가 없기 때문에)
}
