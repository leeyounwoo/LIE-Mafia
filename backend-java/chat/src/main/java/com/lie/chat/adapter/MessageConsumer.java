package com.lie.chat.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.chat.domain.ChatRoom;
import com.lie.chat.dto.CreateChatRoomDto;
import com.lie.chat.port.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@EnableKafka
@RequiredArgsConstructor
public class MessageConsumer {

    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final ChatService chatService;

    @KafkaListener(topics={"create"}, groupId = "chat-group")
    public void createConsume(String message){
        log.info(message);
        try {
            JsonNode jsonNode =objectMapper.readTree(message);
            ChatRoom chatRoom=objectMapper.convertValue(jsonNode.get("room"), ChatRoom.class);
            chatService.createChatRoom(chatRoom);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    @KafkaListener(topics={"join"}, groupId = "chat-group")
    public void joinConsume(String message){
        log.info(message);
    }

    @KafkaListener(topics={"game.start"}, groupId = "chat-group")
    public void startConsume(String message){
        log.info(message);
    }

    @KafkaListener(topics={"leave"}, groupId = "chat-group")
    public void leaveConsume(String message){
        log.info(message);
    }

    @KafkaListener(topics={"close"}, groupId = "chat-group")
    public void closeConsume(String message){
        log.info(message);
    }

    @KafkaListener(topics={"dead"}, groupId = "chat-group")
    public void deadConsume(String message){
        log.info(message);
    }

    @KafkaListener(topics={"game.end"}, groupId = "chat-group")
    public void endConsume(String message){
        log.info(message);
    }


}
