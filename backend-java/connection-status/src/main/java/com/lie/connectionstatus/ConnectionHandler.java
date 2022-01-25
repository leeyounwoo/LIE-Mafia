package com.lie.connectionstatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.connectionstatus.dto.ClientMessageDto;
import com.lie.connectionstatus.port.ConnectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectionHandler extends TextWebSocketHandler {
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final ConnectionService connectionService;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info(message.toString());
        log.info("Socket HI");
        objectMapper.createParser(message.getPayload());

        ClientMessageDto incomingMessage = convertMessageToDto(message.getPayload());

        log.info(incomingMessage.toString());

        switch(incomingMessage.getActionType()){
            case "create" :
                connectionService.createRoom(session,incomingMessage.getUsername());
                break;
            default:
                //response service
                break;
        }


    }

    ClientMessageDto convertMessageToDto(String payload) throws JsonProcessingException {
        return objectMapper.readValue(payload, ClientMessageDto.class);
    }
}
