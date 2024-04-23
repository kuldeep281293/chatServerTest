package com.example.chatservertest.controller;

import com.example.chatservertest.model.Message;
import com.example.chatservertest.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ChatController {
    @Autowired
    private MessageRepository messageRepository;

    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public Message sendMessage(@Payload Message message) {
        messageRepository.save(message);
        return message;
    }

    @MessageMapping("/chat.history")
    @SendTo("/topic/public")
    public List<Message> getHistory() {
        return messageRepository.findAll();
    }
}
