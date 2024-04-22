package com.example.chatservertest;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncodePass {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "password1";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println(encodedPassword);
    }
}
