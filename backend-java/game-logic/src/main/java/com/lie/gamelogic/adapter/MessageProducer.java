package com.lie.gamelogic.adapter;

import lombok.RequiredArgsConstructor;
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

//    public void sendToWebsocketSession(WebSocketSession interfacesession, TextMessage textMessage) throws IOException {
//        interfacesession.sendMessage(textMessage);
//    }
}
