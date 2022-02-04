package com.lie.gamelogic.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishOnKafkaBroker(String topic, String message){
        kafkaTemplate.send(topic, message);
    }
}
