package com.example.chatservertest.controller;

import com.example.chatservertest.model.Message;
import com.example.chatservertest.repository.MessageRepository;
import com.example.chatservertest.security.JwtTokenProvider;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Log4j2
public class ChatController {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public Message sendMessage(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
        // Retrieve token from the WebSocket session
        String token = (String) headerAccessor.getSessionAttributes().get("token");

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromToken(token);

            message.setUsername(username);

            messageRepository.save(message);
            return message;
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
    }

    @MessageMapping("/chat.delete")
    @SendTo("/topic/public")
    public void deleteMessage(@Payload Long messageId, SimpMessageHeaderAccessor headerAccessor) {
        String token = (String) headerAccessor.getSessionAttributes().get("token");
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromToken(token);

            Message message = messageRepository.findById(messageId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));

            if (username.equals(message.getUsername())) {
                messageRepository.deleteById(messageId);
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authorized to delete this message");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
    }

    @MessageMapping("/chat.history")
    @SendTo("/topic/history")
    public List<Message> getHistory() {

        return messageRepository.findAll().stream()
                .filter(message -> !message.isDeleted())
                .collect(Collectors.toList());
    }

}
