package com.lie.gamelogic.port;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.gamelogic.adapter.MessageConsumer;
import com.lie.gamelogic.adapter.MessageProducer;
import com.lie.gamelogic.domain.User;
import com.lie.gamelogic.dto.DeadPersonDto;
import com.lie.gamelogic.dto.GameEndDto;
import com.lie.gamelogic.dto.PressReadyResponseDto;
import com.lie.gamelogic.dto.StartGameDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

//Message를 Producer로 전달하기 위해 거치는 interface
//MessageInterface -> MessageProducer로 이어질 예정
@Component
@RequiredArgsConstructor
public class MessageInterface {
    private final MessageProducer messageProducer;
    private final ObjectMapper objectMapper;

    public void publishReadyEvent(String topic, User user, String roomId){
        PressReadyResponseDto pressReadyResponse = new PressReadyResponseDto(roomId,user);
        try {
            messageProducer.publishOnKafkaBroker(topic, objectMapper.writeValueAsString(pressReadyResponse));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    public void publishStartEvent(String topic, String roomId){
        StartGameDto startGame = new StartGameDto(roomId);
        try{
            messageProducer.publishOnKafkaBroker(topic, objectMapper.writeValueAsString(startGame));
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
    }

    public void publishDeadEvent(String topic, User user, String roomId){
        DeadPersonDto deadPerson = new DeadPersonDto(roomId,user);
        try{
            messageProducer.publishOnKafkaBroker(topic, objectMapper.writeValueAsString(deadPerson));
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
    }

    public void publishGameEndEvent(String topic, GameEndDto gameEndDto){
        try{
            messageProducer.publishOnKafkaBroker(topic, objectMapper.writeValueAsString(gameEndDto));
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
    }

    public void broadcastToRoom(WebSocketSession session, String message){

    }


}
