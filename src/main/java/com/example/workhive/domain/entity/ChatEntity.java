package com.example.workhive.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "chat")
public class ChatEntity {

	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long chatId;

	    @Column(nullable = false)
	    private Long chatroomId;

	    @Column(nullable = false)
	    private String memberId;

	    @Column(nullable = false, length = 255)
	    private String message;

	    @Column(nullable = false, updatable = false)
	    private java.sql.Timestamp sentAt;

	    @Column(nullable = false)
	    private Boolean isDeleted;
}
