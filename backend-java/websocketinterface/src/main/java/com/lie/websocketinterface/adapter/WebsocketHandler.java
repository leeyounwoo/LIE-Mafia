package com.lie.websocketinterface.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.websocketinterface.dto.ClientClosedDataDto;
import com.lie.websocketinterface.dto.EventActionDto;
import com.lie.websocketinterface.dto.InboundMessageDto;
import com.lie.websocketinterface.exception.DuplicateException;
import com.lie.websocketinterface.port.MessageInterface;
import com.lie.websocketinterface.port.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebsocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final MessageInterface messageInterface;
    private final SessionService sessionService;
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        final JsonNode jsonMessage = objectMapper.readTree(message.getPayload());
        final String data = jsonMessage.get("data").toString();
        final EventActionDto eventActionDto = EventActionDto.builder()
                .eventType(jsonMessage.get("eventType").asText())
                .id(jsonMessage.get("data").get("id").asText())
                .build();

        if(eventActionDto.getId().equals("create") || eventActionDto.getId().equals("join")){
            try{
                sessionService.registerSession(session,jsonMessage.get("data").get("username").asText());
            }catch (DuplicateException duplicateException){
                log.info(duplicateException.getMessage());
                afterConnectionClosed(session, CloseStatus.BAD_DATA);
            }
        }

        log.info(eventActionDto.toString());
        log.info(data);
        messageInterface.sendToService(eventActionDto.createTopic(), data, session.getId());


    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        ClientClosedDataDto clientClosedDataDto = new ClientClosedDataDto("leave", session.getId());
        messageInterface.sendToService("connection.leave",objectMapper.writeValueAsString(clientClosedDataDto),session.getId());
        session.close();
        super.afterConnectionClosed(session, status);
    }
}
