package com.jordan.ticket_system.config;

import com.jordan.ticket_system.security.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor
        implements HandshakeInterceptor {

    private final JwtService jwtService;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {

        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return false;
        }

        Cookie[] cookies =
                servletRequest
                        .getServletRequest()
                        .getCookies();

        if (cookies == null) {
            return false;
        }

        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("jwt")) {

                String token = cookie.getValue();

                try {

                    String username =
                            jwtService.extractEmail(token);

                    System.out.println(
                            "WebSocket autenticado: " + username
                    );

                    return true;

                } catch (JwtException e) {

                    System.out.println("JWT inválido");

                    return false;
                }
            }
        }

        System.out.println("JWT no encontrado");

        return false;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {

    }
}