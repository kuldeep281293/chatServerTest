package com.example.chatservertest.config;

import com.example.chatservertest.security.JwtTokenProvider;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Log4j2
public class HttpHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    public HttpHandshakeInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("idhar aaya");
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            String token = servletRequest.getServletRequest().getParameter("token");

            if (token != null && jwtTokenProvider.validateToken(token)) {
                // Put the username and token on the WebSocket session attributes
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                attributes.put("username", auth.getName());
                attributes.put("token", token);
                return true;
            } else {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;
            }
        }

        return false; // Changed to false when the request is not an instance of ServletServerHttpRequest
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // Not required to be implemented if there's nothing specific to do after the handshake
    }
}