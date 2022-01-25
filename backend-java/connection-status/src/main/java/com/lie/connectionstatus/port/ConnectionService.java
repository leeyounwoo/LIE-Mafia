package com.lie.connectionstatus.port;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
public interface ConnectionService {
    public void createRoom(WebSocketSession session, String username);
}
