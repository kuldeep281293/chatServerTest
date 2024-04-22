package com.example.chatservertest.controller;

import com.example.chatservertest.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    public void testLoginSuccess() throws Exception {
        // Setup
        String username = "testUser";
        String password = "password";
        String token = "generatedToken";
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);

        Authentication auth = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(auth);
        given(jwtTokenProvider.createToken(anyString(), anyLong())).willReturn(token);

        // Execute & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.token").value(token))
                .andDo(print());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider).createToken(anyString(), anyLong());
    }

    @Test
    public void testLoginFailure_BadCredentials() throws Exception {
        // Setup
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "user");
        credentials.put("password", "wrongPassword");

        willThrow(new BadCredentialsException("Invalid credentials"))
                .given(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Execute & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(credentials)))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("Invalid username or password", ((ResponseStatusException) result.getResolvedException()).getReason()))
                .andDo(print());
    }

    @Test
    public void testLoginFailure_InternalError() throws Exception {
        // Setup
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "user");
        credentials.put("password", "pass");

        willThrow(new RuntimeException("Unexpected error"))
                .given(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Execute & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(credentials)))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("Internal server error", ((ResponseStatusException) result.getResolvedException()).getReason()))
                .andDo(print());
    }
}
