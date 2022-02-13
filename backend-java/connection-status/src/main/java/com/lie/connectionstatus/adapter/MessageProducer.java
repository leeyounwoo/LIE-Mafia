package com.lie.connectionstatus.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.connectionstatus.domain.user.UserConnection;
import com.lie.connectionstatus.dto.OutboundClientMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageProducer {
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishOnKafkaBroker(String topic, String message){
        kafkaTemplate.send(topic, message);
    }

    public void sendToParticipants(List<UserConnection> participants, String message){
        participants.stream().forEach(participant -> {
            try {
                participant.getSession().sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.info("Sending Message To Session is Not Available");
            }
        });
    }

    public void sendToWebsocketSession(WebSocketSession interfaceSession, TextMessage message) throws IOException {
        interfaceSession.sendMessage(message);
    }
}
