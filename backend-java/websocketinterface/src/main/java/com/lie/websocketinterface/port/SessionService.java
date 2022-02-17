package com.lie.websocketinterface.port;

import com.lie.websocketinterface.dto.OutboundErrorDto;
import com.lie.websocketinterface.dto.OutboundMessageDto;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Service
public interface SessionService {
    void registerSession(WebSocketSession webSocketSession,String username);
    void releaseSession(WebSocketSession webSocketSession);
    void sendMessageToClient(OutboundMessageDto outboundMessageDto);
    void sendErrorMessageToClient(OutboundErrorDto outboundErrorDto) throws IOException;

}
