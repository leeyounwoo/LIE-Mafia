package com.lie.gamelogic.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.gamelogic.domain.Room;
import com.lie.gamelogic.domain.User;
import com.lie.gamelogic.dto.CreateGameRoomDto;
import com.lie.gamelogic.dto.JoinGameRoomDto;
import com.lie.gamelogic.port.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Component
@Slf4j
@EnableKafka
@RequiredArgsConstructor
public class MessageConsumer {
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final GameService gameService;
    // press ready -> connectionservice

    @KafkaListener(topics={"create"}, groupId = "game-group")
    public void createConsume(String message){
        log.info(message);
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            Room room = objectMapper.convertValue(jsonNode.get("room"),Room.class);
            gameService.createGameRoom(room);

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = {"join"}, groupId = "game-group")
    public void joinConsume(String message){
        log.info(message);
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            JoinGameRoomDto joinGameRoomDto = objectMapper.convertValue(jsonNode,JoinGameRoomDto.class);
            log.info(joinGameRoomDto.toString());
            gameService.joinGameRoom(joinGameRoomDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


    }
}
