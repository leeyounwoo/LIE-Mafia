package com.lie.connectionstatus.adapter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.lie.connectionstatus.domain.room.Room;
import com.lie.connectionstatus.domain.room.RoomManager;
import com.lie.connectionstatus.domain.user.UserConnection;
import com.lie.connectionstatus.domain.user.UserConnectionManager;
import com.lie.connectionstatus.dto.ClientMessageDto;
import com.lie.connectionstatus.dto.OutboundErrorDto;
import com.lie.connectionstatus.dto.OutboundMessageDto;
import com.lie.connectionstatus.port.ConnectionService;
import com.lie.connectionstatus.port.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.IceCandidate;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

//redis consumer만 있어도댐
@Component
@Slf4j
@EnableKafka
@RequiredArgsConstructor
public class MessageConsumer {
    private final ObjectMapper objectMapper;
    private final ConnectionService connectionService;
    private final UserConnectionManager userConnectionManager;
    private final RoomManager roomManager;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final SessionService sessionService;


    @KafkaListener(topics={"client.response"}, groupId = "connection-group")
    public void responseConsume(String message){
        log.info(message);
        try {
            OutboundMessageDto outboundMessageDto = makeOutboudMessageDto(message);
            sessionService.sendMessageToClient(makeOutboudMessageDto(message));
        } catch (JsonProcessingException e) {
            log.info("Error making outbound message");
            log.info("Nothing sent to clients");
            log.info(e.getMessage());
        }
    }
    @KafkaListener(topics={"error"}, groupId = "connection-group")
    public void errorConsume(String message){
        log.info(message);
        try {
            OutboundErrorDto outboundErrorDto = objectMapper.convertValue(message, OutboundErrorDto.class);
            sessionService.sendErrorMessageToClient(outboundErrorDto);
        } catch (JsonProcessingException e) {
            log.info("Error making outbound message");
            log.info("Nothing sent to clients");
            log.info(e.getMessage());
        } catch (IOException e){
            log.info(e.getMessage());
        }


    }
    public OutboundMessageDto makeOutboudMessageDto(String message) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(message.replaceAll("\\n","").replaceAll(" ", ""));
        return OutboundMessageDto.builder()
                .receivers(objectMapper.readValue(jsonNode.get("receivers").toString(), TypeFactory.defaultInstance().constructCollectionType(List.class,String.class)))
                .message(jsonNode.get("message").asText()).build();
    }
//
//    @KafkaListener(topics={"connection.create"}, groupId = "connection-test-group-3")
//    public void createConsume(String message)throws JsonProcessingException{
//        JsonNode jsonMessage = objectMapper.readTree(message);
//        ClientMessageDto clientMessageDto = new ClientMessageDto(jsonMessage,objectMapper);
//        try{
//            connectionService.createRoom(clientMessageDto.getSessionId(),
//                    clientMessageDto.getUsername());
//        }catch (Exception e){
//            log.info("Error in creating");
//        }
//    }
//
//    @KafkaListener(topics={"connection.join"}, groupId = "connection-test-group-3")
//    public void joinConsume(String message) throws JsonProcessingException {
//        JsonNode jsonMessage = objectMapper.readTree(message);
//        ClientMessageDto clientMessageDto = new ClientMessageDto(jsonMessage,objectMapper);
//        try{
//            connectionService.joinRoom(clientMessageDto.getSessionId(),
//                    clientMessageDto.getUsername(),
//                    clientMessageDto.getRoomId());
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    @KafkaListener(topics={"connection.leave"}, groupId = "connection-test-group-3")
//    public void leaveConsume(String message)throws JsonProcessingException{
//        JsonNode jsonMessage = objectMapper.readTree(message);
//        ClientMessageDto clientMessageDto = new ClientMessageDto(jsonMessage,objectMapper);
//        try{
//            connectionService.leaveRoom(clientMessageDto.getSessionId());
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    @KafkaListener(topics={"connection.receiveVideoFrom"}, groupId = "connection-test-group-3")
//    public void receiveVideoFromConsume(String message)throws JsonProcessingException{
//        JsonNode jsonMessage = objectMapper.readTree(message);
//        ClientMessageDto clientMessageDto = new ClientMessageDto(jsonMessage,objectMapper);
//        final UserConnection user = userConnectionManager.getBySession(clientMessageDto.getSessionId());
//
//        final String senderName = clientMessageDto.getSender();
//        final UserConnection sender = userConnectionManager.getByUsername(senderName);
//        final String sdpOffer = clientMessageDto.getSdpOffer();
//
//        try{
//            user.receiveVideoFrom(sender, sdpOffer);
//        }catch (IOException ioException){
//            ioException.printStackTrace();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    @KafkaListener(topics={"connection.onIceCandidate"}, groupId = "connection-test-group-3")
//    public void onIceCandidatreConsume(String message)throws JsonProcessingException{
//        JsonNode jsonMessage = objectMapper.readTree(message);
//        ClientMessageDto clientMessageDto = new ClientMessageDto(jsonMessage,objectMapper);
//        final UserConnection user = userConnectionManager.getBySession(clientMessageDto.getSessionId());
//
//        if(user != null){
//            IceCandidate candidate = new IceCandidate(clientMessageDto.getCandidate().getCandidate(),
//                    clientMessageDto.getCandidate().getSdpMid(),
//                    clientMessageDto.getCandidate().getSdpMLineIndex());
//            try{
//                user.addCandidate(candidate, clientMessageDto.getName());
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//    }

//    @KafkaListener(topics={"start"}, groupId = "connection-group")
//    public void startConsume(String message)throws JsonProcessingException{
//        final JsonNode jsonMessage = objectMapper.readTree(message);
//
//        ClientMessageDto clientMessageDto = new ClientMessageDto(jsonMessage,objectMapper);
//        log.info(clientMessageDto.toString());
//
//        final UserConnection user = userConnectionManager.getBySession(clientMessageDto.getSessionId());
//
//
//    }

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
