package com.example.chatservertest.repository;

import com.example.chatservertest.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MessageRepository extends JpaRepository<Message, Long> {}
