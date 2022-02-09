package com.lie.websocketinterface.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.websocketinterface.dto.ClientClosedDataDto;
import com.lie.websocketinterface.dto.InboundMessageDto;
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
    private final MessageProducer messageProducer;
    private final SessionService sessionService;
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        final JsonNode jsonMessage = objectMapper.readTree(message.getPayload());
        final InboundMessageDto inboundMessage = InboundMessageDto.builder()
                                                    .eventType(jsonMessage.get("eventType").asText())
                                                    .data(jsonMessage.get("data").toString())
                                                    .build();

        log.info(inboundMessage.toString());


        switch(inboundMessage.getEventType()){
            case "connection":
                log.info("this is for connection server");
                log.info(inboundMessage.getData());
                log.info(session.toString()+"handler");
                if(jsonMessage.get("data").get("id").asText().equals("create") || jsonMessage.get("data").get("id").asText().equals("join")){
                    sessionService.registerSession(session,jsonMessage.get("data").get("username").asText());
                }
                messageProducer.sendToService(inboundMessage.getEventType(), inboundMessage.getData(), session.getId());
                break;
            case "game":
                log.info("this is for game logic server");
                log.info(inboundMessage.getData());
                messageProducer.sendToService(inboundMessage.getEventType(), inboundMessage.getData(), session.getId());

                break;
            case "chat":
                log.info("this is for chat server");
                log.info(inboundMessage.getData());
                break;
        }

    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        super.handlePongMessage(session, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        ClientClosedDataDto clientClosedDataDto = new ClientClosedDataDto("leave", session.getId());

        messageProducer.sendToService("connection",objectMapper.writeValueAsString(clientClosedDataDto),session.getId());
        super.afterConnectionClosed(session, status);
    }
}
