package com.lie.connectionstatus.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.connectionstatus.domain.user.UserConnection;
import com.lie.connectionstatus.domain.user.UserConnectionManager;
import com.lie.connectionstatus.domain.room.RoomManager;
import com.lie.connectionstatus.dto.ClientMessageDto;
import com.lie.connectionstatus.dto.ErrorMessageDto;
import com.lie.connectionstatus.dto.InboundMessageDto;
import com.lie.connectionstatus.port.ConnectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.kurento.client.IceCandidate;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectionHandler extends TextWebSocketHandler {
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final ConnectionService connectionService;
    private final UserConnectionManager userConnectionManager;
    private final RoomManager roomManager;
    private final KafkaTemplate<String, String> kafkaTemplate;
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        final JsonNode jsonMessage = objectMapper.readTree(message.getPayload());

        log.info(session.getId());
        log.info(jsonMessage.toString());
        String data = jsonMessage.get("data").asText();
        JsonNode finalMessage = objectMapper.readTree(data);
        log.info(data);
        ClientMessageDto clientMessage = new ClientMessageDto(jsonMessage, objectMapper);
        final UserConnection user = userConnectionManager.getBySession(clientMessage.getSessionId());

        log.info(clientMessage.toString());

        switch(clientMessage.getId()){
            case "create" :
                try{
                    connectionService.createRoom(session, clientMessage.getSessionId(),
                            clientMessage.getUsername());
                }catch (Exception e){
                    log.info("Error in creating");
                }
                break;
            case "join" :
                try{
                    connectionService.joinRoom(session, clientMessage.getSessionId(),
                        clientMessage.getUsername(),
                        clientMessage.getRoomId());
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case "leave" :
                try{
                    log.info("Client Leaving");
                    connectionService.leaveRoom(session, clientMessage.getSessionId());
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case "receiveVideoFrom":
                final String senderName = clientMessage.getSender();
                final UserConnection sender = userConnectionManager.getByUsername(senderName);
                final String sdpOffer = clientMessage.getSdpOffer();

                try{
                    user.receiveVideoFrom(session, sender, sdpOffer);
                }catch (IOException ioException){
                    ioException.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case "onIceCandidate" :
                if(user != null){
                    IceCandidate candidate = new IceCandidate(clientMessage.getCandidate().getCandidate(),
                            clientMessage.getCandidate().getSdpMid(),
                            clientMessage.getCandidate().getSdpMLineIndex());
                    try{
                        user.addCandidate(candidate, clientMessage.getName());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
        }

    }
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        session.close();
        kafkaTemplate.send("disconnection", objectMapper.writeValueAsString(ErrorMessageDto.builder()
                .service("connection")
                .status("TRANSPORT ERROR")
                .build()));
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info(status.getReason());

    }


    ClientMessageDto convertMessageToDto(String payload) throws JsonProcessingException {
        return objectMapper.readValue(payload, ClientMessageDto.class);
    }
}
