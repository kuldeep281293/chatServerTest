package com.example.chatservertest.controller;

import com.example.chatservertest.model.Message;
import com.example.chatservertest.repository.MessageRepository;
import com.example.chatservertest.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ChatControllerTests {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private ChatController chatController;

    @Mock
    private SimpMessageHeaderAccessor headerAccessor;

    private Message message;

    @BeforeEach
    void setUp() {
        message = new Message();
        message.setId(1L);
        message.setContent("Hello World");
        message.setUsername("user1");
    }

    @Test
    void sendMessage_ValidToken_ReturnsMessage() {
        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put("token", "validToken");
        when(headerAccessor.getSessionAttributes()).thenReturn(sessionAttributes);
        when(jwtTokenProvider.validateToken("validToken")).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken("validToken")).thenReturn("user1");
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        Message returnedMessage = chatController.sendMessage(message, headerAccessor);

        assertNotNull(returnedMessage);
        assertEquals("user1", returnedMessage.getUsername());
        verify(messageRepository).save(message);
    }

    @Test
    void sendMessage_InvalidToken_ThrowsException() {
        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put("token", "invalidToken");
        when(headerAccessor.getSessionAttributes()).thenReturn(sessionAttributes);
        when(jwtTokenProvider.validateToken("invalidToken")).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> {
            chatController.sendMessage(message, headerAccessor);
        }, "Invalid or expired token");
    }

    @Test
    void deleteMessage_ValidToken_DeletesMessage() {
        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put("token", "validToken");
        when(headerAccessor.getSessionAttributes()).thenReturn(sessionAttributes);
        when(jwtTokenProvider.validateToken("validToken")).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken("validToken")).thenReturn("user1");
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));

        assertDoesNotThrow(() -> chatController.deleteMessage(1L, headerAccessor));
        verify(messageRepository).deleteById(1L);
    }

    @Test
    void deleteMessage_InvalidToken_ThrowsException() {
        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put("token", "invalidToken");
        when(headerAccessor.getSessionAttributes()).thenReturn(sessionAttributes);
        when(jwtTokenProvider.validateToken("invalidToken")).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> {
            chatController.deleteMessage(1L, headerAccessor);
        }, "Invalid or expired token");
    }

    @Test
    void getHistory_ReturnsMessages() {
        when(messageRepository.findAll()).thenReturn(Arrays.asList(message));

        List<Message> messages = chatController.getHistory();

        assertNotNull(messages);
        assertFalse(messages.isEmpty());
        assertEquals(1, messages.size());
        assertEquals("Hello World", messages.get(0).getContent());
    }
}
