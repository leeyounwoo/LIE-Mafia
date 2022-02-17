package com.lie.gamelogic.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Log4j2
@Component
@RequiredArgsConstructor
public class MessageProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishOnKafkaBroker(String topic, String message){
        log.info(message);
        kafkaTemplate.send(topic, message);
    }

//    public void sendToWebsocketSession(WebSocketSession interfacesession, TextMessage textMessage) throws IOException {
//        interfacesession.sendMessage(textMessage);
//    }
}
