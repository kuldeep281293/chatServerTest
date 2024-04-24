package com.example.chatservertest.controller;

import com.example.chatservertest.model.Message;
import com.example.chatservertest.repository.MessageRepository;
import com.example.chatservertest.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Controller
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
            // Extract username from token
            String username = jwtTokenProvider.getUsernameFromToken(token);

            // Set the username to the message
            message.setUsername(username);

            // Save the message with the username to the database
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
    public void getHistory(SimpMessageHeaderAccessor headerAccessor) {
        String token = (String) headerAccessor.getSessionAttributes().get("token");
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            List<Message> history = messageRepository.findAll().stream()
                    .filter(message -> !message.isDeleted())
                    .collect(Collectors.toList());
            // Send the history to a user-specific topic
            headerAccessor.getSession().send("/topic/history-" + username, history);
        }
    }

}
