package com.lie.connectionstatus.adapter;

import com.lie.connectionstatus.dto.OutboundClientMessageDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class MessageProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishOnKafkaBroker(String topic, String message){
        kafkaTemplate.send(topic, message);
    }

    public void sendToWebsocketSession(WebSocketSession interfaceSession, TextMessage message) throws IOException {
        interfaceSession.sendMessage(message);
    }
}
