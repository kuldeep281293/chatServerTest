package com.example.chatservertest.controller;

import com.example.chatservertest.model.Token;
import com.example.chatservertest.repository.TokenRepository;
import com.example.chatservertest.security.JwtTokenProvider;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@Log4j2
public class AuthenticationController {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenRepository tokenRepository;

    @PostMapping("/auth/login")
    public Map<String, String> login(@RequestBody Map<String, String> user) {
        try {
            log.debug("Attempting to authenticate user: {}", user.get("username"));

            String username = user.get("username");
            String password = user.get("password");

            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = tokenProvider.createToken(username, 86400000l);
            Token existingToken = tokenRepository.findByUsername(username)
                    .orElse(new Token()); // Use the Token class constructor

            existingToken.setUsername(username);
            existingToken.setToken(token);

            tokenRepository.save(existingToken); // This will update if exists, or save a new one otherwise

            Map<String, String> response = new HashMap<>();
            response.put("username", username);
            response.put("token", token);
            return response;
        } catch (BadCredentialsException e) {
            log.error("Invalid username or password", e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        } catch (AuthenticationException e) {
            log.error("Authentication failed", e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failure");
        } catch (Exception e) {
            log.error("Login error", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e);
        }
    }

}
