package com.example.chatservertest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Value("${app.user-details}")
    private String userDetails;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return Arrays.stream(userDetails.split(","))
                .map(this::parseUserDetails)
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private UserDetails parseUserDetails(String userDetails) {
        String[] parts = userDetails.split(":");
        if (parts.length < 2) {
            throw new IllegalArgumentException("User details must be in the format username:password");
        }
        return new User(parts[0], parts[1], List.of(new SimpleGrantedAuthority("USER")));
    }
}
