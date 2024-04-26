package com.example.chatservertest;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncodePass {
    public static void main(String[] args) {
        // used to geenrated encoded password for hardcoding
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "password2";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println(encodedPassword);
    }
}
