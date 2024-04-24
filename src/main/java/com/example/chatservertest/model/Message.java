package com.example.chatservertest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.time.LocalDateTime;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String content;
    private LocalDateTime timestamp = LocalDateTime.now();
    private boolean isDeleted = false;

    public Message(Long messageId, String username, String content, boolean isDeleted) {
        this.id = messageId;
        this.username = username;
        this.content  = content;
        this.isDeleted = isDeleted;
    }
}

