package com.lie.gamelogic.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.gamelogic.domain.Room;
import com.lie.gamelogic.domain.RoomPhase;
import com.lie.gamelogic.dto.*;
import com.lie.gamelogic.port.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@EnableKafka
@RequiredArgsConstructor
public class MessageConsumer {
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

    @KafkaListener(topics = {"leave"}, groupId = "game-group")
    public void leaveConsume(String message){
        log.info(message);
        try{
            JsonNode jsonNode = objectMapper.readTree(message);
            log.info(jsonNode.toString());
            gameService.leaveGameRoom(jsonNode.get("username").asText(), jsonNode.get("roomId").asText());
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = {"close"}, groupId = "game-group")
    public void closeConsume(String message){
        log.info(message);
        try{
            JsonNode jsonNode = objectMapper.readTree(message);
            log.info(jsonNode.get("data").toString());
            gameService.closeGameRoom(jsonNode.get("data").asText());
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = {"game.ready"}, groupId = "game-group")
    public void readyConsume(String message){
        final JsonNode jsonMessage;
        try {
            jsonMessage = objectMapper.readTree(message);
            ClientMessageDto clientMessageDto = new ClientMessageDto(jsonMessage,objectMapper);

            gameService.pressReady(clientMessageDto.getSessionId(),
                        clientMessageDto.getRoomId(),
                       clientMessageDto.getUsername());


        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //session 관리는 이후 api gateway에서 작업할 예정이지만, 테스트를 위해
    }

    @KafkaListener(topics = {"game.start"}, groupId = "game-group")
    public void startConsume(String message){
        final JsonNode jsonMessage;
        try {
            jsonMessage = objectMapper.readTree(message);
            ClientMessageDto clientMessageDto = new ClientMessageDto(jsonMessage,objectMapper);

            log.info(clientMessageDto.toString());
            gameService.pressStart(clientMessageDto.getSessionId(),
                    clientMessageDto.getRoomId(),
                    clientMessageDto.getUsername());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = {"game.nightVote"}, groupId = "game-group")
    public void nightVoteConsume(String message){
        final JsonNode jsonMessage;
        try {
            jsonMessage = objectMapper.readTree(message);
            ClientMessageVoteDto clientMessageVoteDto = new ClientMessageVoteDto(jsonMessage,objectMapper);

            log.info(clientMessageVoteDto.toString());

            gameService.selectNightVote(clientMessageVoteDto.getSessionId(),
                    clientMessageVoteDto.getRoomId(),
                    clientMessageVoteDto.getUsername(),
                    clientMessageVoteDto.getSelect());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = {"game.executionVote"}, groupId = "game-group")
    public void ExceptionVoteConsume(String message){
        final JsonNode jsonMessage;
        try {
            jsonMessage = objectMapper.readTree(message);
            ClientMessageExceptionVoteDto clientMessageVoteDto = new ClientMessageExceptionVoteDto(jsonMessage,objectMapper);
            RoomPhase roomPhase = null;
           if(clientMessageVoteDto.getRoomPhase().equals("citizenVote")){
               roomPhase = RoomPhase.EXECUTIONVOTE;
           }

            log.info(clientMessageVoteDto.toString());
            gameService.selectExecutionVote(clientMessageVoteDto.getSessionId(),
                    clientMessageVoteDto.getRoomId(),
                    clientMessageVoteDto.getUsername(),
                    clientMessageVoteDto.getSelect(),
                    roomPhase,
                    clientMessageVoteDto.getAgreeToDead()
                    );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @KafkaListener(topics = {"game.citizenVote"}, groupId = "game-group")
    public void CitizenVoteConsume(String message){

        log.info(message);
        final JsonNode jsonMessage;
        try {
            jsonMessage = objectMapper.readTree(message);
            ClientMessageVoteDto clientMessageVoteDto = new ClientMessageVoteDto(jsonMessage,objectMapper);

            log.info(clientMessageVoteDto.toString());
            gameService.selectMoringVote(clientMessageVoteDto.getSessionId(),
                    clientMessageVoteDto.getRoomId(),
                    clientMessageVoteDto.getUsername(),
                    clientMessageVoteDto.getSelect());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = {"game.dead"}, groupId = "game-group")
    public void deadConsume(String message) {log.info(message); }

    @KafkaListener(topics = {"game.end"}, groupId = "game-group")
    public void EndConsume(String message) {log.info(message); }

}
