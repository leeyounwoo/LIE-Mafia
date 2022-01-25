package com.lie.connectionstatus.config;

import com.lie.connectionstatus.adapter.ConnectionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@RequiredArgsConstructor
@EnableWebSocket
public class ConnectionSocketConfig implements WebSocketConfigurer {
    public final ConnectionHandler connectionHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(connectionHandler, "/connect").setAllowedOrigins("*");
    }
}
