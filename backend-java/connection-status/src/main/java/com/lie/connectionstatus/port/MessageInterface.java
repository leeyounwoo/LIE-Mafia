package com.lie.connectionstatus.port;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.connectionstatus.adapter.MessageProducer;
import com.lie.connectionstatus.domain.user.User;
import com.lie.connectionstatus.domain.user.UserConnection;
import com.lie.connectionstatus.domain.user.UserConnectionManager;
import com.lie.connectionstatus.domain.room.Room;
import com.lie.connectionstatus.dto.OutboundClientMessageDto;
import com.lie.connectionstatus.dto.OutboundToServiceMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.internal.util.Iterables;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageInterface {
    private final UserConnectionManager userConnectionManager;
    private final MessageProducer messageProducer;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishEventToKafka(String topic, String message){
        kafkaTemplate.send(topic, message);
    }

    public void broadCastToClient(String topic, Map<String, User> sessionIds, String message) {
        List<String> clients = sessionIds.values()
                .stream().map(user -> user.getSessionId())
                .collect(Collectors.toList());
        OutboundClientMessageDto outboundClientMessageDto = new OutboundClientMessageDto(clients, message);
        try{
            messageProducer.publishOnKafkaBroker(topic, objectMapper.writeValueAsString(outboundClientMessageDto));
        } catch (JsonProcessingException jsonProcessingException){
            log.info("Error Processing Json");
        }
    }

    public void broadCastToClient(String topic, String sessionId, String message) {
        ArrayList<String> client = new ArrayList<>();
        client.add(sessionId);
        OutboundClientMessageDto outboundClientMessageDto = new OutboundClientMessageDto(client,message);
        try{
            messageProducer.publishOnKafkaBroker(topic, objectMapper.writeValueAsString(outboundClientMessageDto));
        } catch (JsonProcessingException jsonProcessingException){
            log.info("Error Processing Json");
        }
    }

    public void broadcastToExistingParticipants(Room room, String message){
        room.getParticipants().values().stream()
                .map(user -> userConnectionManager.getUsersBySessionId().get(user.getSessionId()))
                .map(userConnection -> userConnection.getSession())
                .forEach(session -> {
                    try {
                        session.sendMessage(new TextMessage(message));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void broadcastToNewParticipants(UserConnection newParticipant, String message){
        try{
            newParticipant.getSession().sendMessage(new TextMessage(message));
        } catch (IOException e){
            log.info("Error in broadcastToNewParticipants. Layer MessageInterface");
        }

    }

    public void broadcastToRoom(Room room, String message){
        room.getParticipants().values().stream()
                .map(user -> userConnectionManager.getUsersBySessionId().get(user.getSessionId()))
                .map(userConnection -> userConnection.getSession())
                .forEach(session -> {
                    try {
                        session.sendMessage(new TextMessage(message));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
    public void sendToService(String topic, String data, String sessionId) throws IOException {
        OutboundToServiceMessageDto outboundToServiceMessageDto = new OutboundToServiceMessageDto(sessionId,data);
        messageProducer.publishOnKafkaBroker(topic, objectMapper.writeValueAsString(outboundToServiceMessageDto));
    }

}
