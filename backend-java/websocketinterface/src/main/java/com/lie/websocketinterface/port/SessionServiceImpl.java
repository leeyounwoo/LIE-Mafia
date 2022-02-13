package com.lie.websocketinterface.port;

import com.lie.websocketinterface.adapter.MessageProducer;
import com.lie.websocketinterface.domain.SessionManager;
import com.lie.websocketinterface.dto.OutboundErrorDto;
import com.lie.websocketinterface.dto.OutboundMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class SessionServiceImpl implements SessionService{
    private final SessionManager sessionManager;
    private final MessageProducer messageProducer;
    @Override
    public void registerSession(WebSocketSession webSocketSession, String username) {
        log.info(webSocketSession.toString() + " service impl");
        sessionManager.registerSession(webSocketSession, username);
    }

    @Override
    public void releaseSession(WebSocketSession webSocketSession) {
        sessionManager.removeBySession(webSocketSession);
    }

    @Override
    public void sendMessageToClient(OutboundMessageDto outboundMessageDto) {
        messageProducer.sendToParticipants(outboundMessageDto.getReceivers().stream()
                .map(receiver -> sessionManager.getBySessionId(receiver))
                .collect(Collectors.toList()), outboundMessageDto.getMessage());
    }

    @Override
    public void sendErrorMessageToClient(OutboundErrorDto outboundErrorDto) throws IOException {
        WebSocketSession clientSession = sessionManager.getBySessionId(outboundErrorDto.getSessionId());
        clientSession.close(CloseStatus.BAD_DATA);
    }
}

