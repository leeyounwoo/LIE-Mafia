package com.lie.gamelogic.port;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.gamelogic.adapter.MessageProducer;
import com.lie.gamelogic.domain.Room;
import com.lie.gamelogic.domain.User;
import com.lie.gamelogic.dto.*;
import com.lie.gamelogic.dto.Start.StartGameDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//Message를 Producer로 전달하기 위해 거치는 interface
//MessageInterface -> MessageProducer로 이어질 예정
@Component
@RequiredArgsConstructor
@Log4j2
public class MessageInterface {
    private final MessageProducer messageProducer;
    private final ObjectMapper objectMapper;

    public void publishReadyEvent(String topic, Room room,String message){
        List<String> clients = room.getParticipants().values()
                .stream().map(user -> user.getSessionId())
                .collect(Collectors.toList());
        OutboundClientMessageDto outboundClientMessageDto = new OutboundClientMessageDto(clients, message,null);
        try {
            messageProducer.publishOnKafkaBroker(topic, objectMapper.writeValueAsString(outboundClientMessageDto));
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
    //전체에게 보내줌
    public void publishReponseEvent(String topic, Room room, String message){
        List<String> clients = room.getParticipants().values()
                .stream().map(user -> user.getSessionId())
                .collect(Collectors.toList());
        OutboundClientMessageDto outboundClientMessageDto = new OutboundClientMessageDto(clients, message ,null);
        try{
            messageProducer.publishOnKafkaBroker(topic, objectMapper.writeValueAsString(outboundClientMessageDto));
        } catch (JsonProcessingException jsonProcessingException){
            log.info("Error Processing Json");
        } catch (IOException ioException){
            log.info("Error Sending Through Websocket");
        }
    }
    public void publishReponseEvent(String topic, User user, String message){
        ArrayList<String> client = new ArrayList<>();
        client.add(user.getSessionId());
        OutboundClientMessageDto outboundClientMessageDto = new OutboundClientMessageDto(client, message ,null);
        try{
            messageProducer.publishOnKafkaBroker(topic, objectMapper.writeValueAsString(outboundClientMessageDto));
        } catch (JsonProcessingException jsonProcessingException){
            log.info("Error Processing Json");
        } catch (IOException ioException){
            log.info("Error Sending Through Websocket");
        }
    }
    public void publishReponseEvent(String topic, List<User> JobList, String message){
        List<String> clients = JobList.stream().map(
                        user -> user.getSessionId())
                .collect(Collectors.toList());
        OutboundClientMessageDto outboundClientMessageDto = new OutboundClientMessageDto(clients, message);
        try{
            messageProducer.publishOnKafkaBroker(topic, objectMapper.writeValueAsString(outboundClientMessageDto));
        } catch (JsonProcessingException jsonProcessingException){
            log.info("Error Processing Json");
        } catch (IOException ioException){
            log.info("Error Sending Through Websocket");
        }
    }



//    //전체에게 보내주는 것
//    public void broadCastToClient(WebSocketSession interfaceSession, Room room, String message) {
//
//    }
//    //한명에게 보내주는 것
//    public void broadCastToClient(WebSocketSession interfaceSession, User user, String message) {
//        ArrayList<String> client = new ArrayList<>();
//        client.add(user.getSessionId());
//
//        OutboundClientMessageDto outboundClientMessageDto = new OutboundClientMessageDto(client,message, null);
//        try{
//            messageProducer.sendToWebsocketSession(interfaceSession, new TextMessage(objectMapper.writeValueAsString(outboundClientMessageDto)));
//        } catch (JsonProcessingException jsonProcessingException){
//            log.info("Error Processing Json");
//        } catch (IOException ioException){
//            log.info("Error Sending Through Websocket");
//        }
//    }
//
//    //특정 직업 사람들에게 보내주는 것
//    public void broadCastToClient(WebSocketSession interfaceSession, List<User> JobList, String message) {
//
//    }


}
