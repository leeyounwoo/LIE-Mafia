package com.lie.websocketinterface.domain;

import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentMap;


public class SessionManager {
    private ConcurrentMap<String, WebSocketSession> sessionBySessionId;
}
