package com.lie.websocketinterface.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.lie.websocketinterface.domain.SessionManager;
import com.lie.websocketinterface.dto.OutboundMessageDto;
import com.lie.websocketinterface.port.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


@Component
@Slf4j
@RequiredArgsConstructor
public class ServiceClient {
    private final ObjectMapper objectMapper;
    private final SessionManager sessionManager;

    @PostConstruct
    @Bean(name = "connectionServiceSession")
    public WebSocketSession connectToConnectionService() throws ExecutionException, InterruptedException {
        WebSocketClient connectionClient = new StandardWebSocketClient();
        WebSocketSession connectionSession = connectionClient.doHandshake(new TextWebSocketHandler() {
            @Override
            public void handleTextMessage(WebSocketSession session, TextMessage message) throws JsonProcessingException {
                JsonNode jsonNode = objectMapper.readTree(message.getPayload().replaceAll("\\n","").replaceAll(" ", ""));

                OutboundMessageDto outboundMessageDto = OutboundMessageDto.builder()
                        .receivers(objectMapper.readValue(jsonNode.get("receivers").toString(), TypeFactory.defaultInstance().constructCollectionType(List.class,String.class)))
                        .message(jsonNode.get("message").asText()).build();


                sendMessage(outboundMessageDto);
            }
            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
                session.close();
            }

            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
            }
        }, new WebSocketHttpHeaders(), URI.create("ws://52.79.223.21:8080/connect")).get();

        return  connectionSession;
    }

    @PostConstruct
    @Bean(name = "gameServiceSession")
    public WebSocketSession connectToGameService() throws ExecutionException, InterruptedException {
        WebSocketClient gameClient = new StandardWebSocketClient();
        WebSocketSession gameSession = gameClient.doHandshake(new TextWebSocketHandler() {
            @Override
            public void handleTextMessage(WebSocketSession session, TextMessage message) throws JsonProcessingException {
                JsonNode jsonNode = objectMapper.readTree(message.getPayload().replaceAll("\\n","").replaceAll(" ", ""));


                OutboundMessageDto outboundMessageDto = OutboundMessageDto.builder()
                        .receivers(objectMapper.readValue(jsonNode.get("receivers").toString(), TypeFactory.defaultInstance().constructCollectionType(List.class,String.class)))
                        .message(jsonNode.get("message").asText()).build();


                sendMessage(outboundMessageDto);
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
                session.close();
            }

            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
            }
        }, new WebSocketHttpHeaders(), URI.create("ws://52.79.223.21:8082/game")).get();

        return  gameSession;
    }
    private void sendMessage(OutboundMessageDto outboundMessageDto){
        outboundMessageDto.getReceivers().stream()
                .map(receiver -> sessionManager.getBySessionId(receiver))
                .forEach(receiver -> {
                    try{
                        receiver.sendMessage(new TextMessage(outboundMessageDto.getMessage()));
                    }catch (IOException e){
                        log.info("Sending to session not available");
                    }
                });
    }

/*
    @PostConstruct
    public WebSocketSession connectToChatService() throws ExecutionException, InterruptedException {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketSession webSocketSession = webSocketClient.doHandshake(new TextWebSocketHandler() {
            @Override
            public void handleTextMessage(WebSocketSession session, TextMessage message) {
                // Services to Clients
            }
            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
            }
        }, new WebSocketHttpHeaders(), URI.create("ws://192.168.0.2:8080/")).get();

        return  webSocketSession;
    }*/
}
