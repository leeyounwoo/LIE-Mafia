package com.lie.connectionstatus.adapter;

import com.lie.connectionstatus.domain.room.Room;
import com.lie.connectionstatus.port.ConnectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//redis consumer만 있어도댐
@Component
@Slf4j
@EnableKafka
public class MessageConsumer {
    // press ready -> connectionservice
/*
    @KafkaListener(topics={"create"}, groupId = "connection-group")
    public void createConsume(String message){
        log.info(message);
    }

    @KafkaListener(topics = {"join"}, groupId = "connection-group")
    public void joinConsume(String message){
        log.info(message);
    }*/
}
