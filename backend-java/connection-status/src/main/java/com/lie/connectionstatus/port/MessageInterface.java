package com.lie.connectionstatus.port;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.connectionstatus.domain.user.UserConnection;
import com.lie.connectionstatus.domain.user.UserConnectionManager;
import com.lie.connectionstatus.domain.room.Room;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageInterface {
    private final UserConnectionManager userConnectionManager;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishEventToKafka(String topic, String message){
        kafkaTemplate.send(topic, message);
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

    public void broadcastToNewParticipants(UserConnection newParticipant, String message) {
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
}
