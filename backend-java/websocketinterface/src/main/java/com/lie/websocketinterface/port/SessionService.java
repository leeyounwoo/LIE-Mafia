package com.lie.websocketinterface.port;

import com.lie.websocketinterface.dto.OutboundMessageDto;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
public interface SessionService {
    void registerSession(WebSocketSession webSocketSession,String username);
    void releaseSession(WebSocketSession webSocketSession);
    void sendMessageToClient(OutboundMessageDto outboundMessageDto);
}
