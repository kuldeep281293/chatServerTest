package com.example.chatservertest.model;

import jakarta.persistence.*;

import java.sql.Timestamp;


@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private Timestamp timestamp;

    @ManyToOne
    private User user;  // Link to the user who sent the message

    // Standard getters and setters
}
