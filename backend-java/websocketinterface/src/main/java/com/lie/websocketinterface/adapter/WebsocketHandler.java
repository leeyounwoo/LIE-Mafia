package com.lie.websocketinterface.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.websocketinterface.dto.ClientClosedDataDto;
import com.lie.websocketinterface.dto.EventActionDto;
import com.lie.websocketinterface.dto.InboundMessageDto;
import com.lie.websocketinterface.port.MessageInterface;
import com.lie.websocketinterface.port.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

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
            sessionService.registerSession(session,jsonMessage.get("data").get("username").asText());
        }

        log.info(eventActionDto.toString());
        log.info(data);

        messageInterface.sendToService(eventActionDto.createTopic(), data, session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        ClientClosedDataDto clientClosedDataDto = new ClientClosedDataDto("connection.leave", session.getId());
        //messageProducer.sendToService("connection",objectMapper.writeValueAsString(clientClosedDataDto),session.getId());
        super.afterConnectionClosed(session, status);
    }
}
