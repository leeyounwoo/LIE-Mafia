package com.lie.connectionstatus.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.lie.connectionstatus.domain.UserConnection;
import com.lie.connectionstatus.domain.UserConnectionManager;
import com.lie.connectionstatus.domain.room.RoomManager;
import com.lie.connectionstatus.dto.ClientMessageDto;
import com.lie.connectionstatus.port.ConnectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.IceCandidate;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectionHandler extends TextWebSocketHandler {
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final ConnectionService connectionService;
    private final UserConnectionManager userConnectionManager;
    private final RoomManager roomManager;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        final JsonNode jsonMessage = objectMapper.readTree(message.getPayload());

        final UserConnection user = userConnectionManager.getBySession(session); //registry (Redis or ConcurrentMap)
        //"connection"은 어차피 체크될것, id -> actionType으로 변경하여 메시지 컨벤션 맞추기
        //아래 수행문들 모두 connectionService로 들어가서 작업
        log.info(jsonMessage.toString());
        log.info(session.getId());

        switch(jsonMessage.get("id").asText()){
            //create로 바꾸기
            case "create" :
                try{
                    connectionService.createRoom(session,
                            jsonMessage.get("username").asText());
                }catch (Exception e){
                    e.printStackTrace();
                    break;
                }
                break;
            case "join" :
                connectionService.joinRoom(session,
                        jsonMessage.get("username").asText(),
                        jsonMessage.get("roomId").asText());
                break;
            case "receiveVideoFrom":
                //sender -> sessionId로 보내면 좋을 듯
                final String senderName = jsonMessage.get("sender").asText();
                //방 사이 닉네임 유효한 부분 있는지 확인 - session으로 바꿔줘야하지 않나 싶음
                final UserConnection sender = userConnectionManager.getByUsername(senderName);
                final String sdpOffer = jsonMessage.get("sdpOffer").asText();
                user.receiveVideoFrom(sender, sdpOffer);

            case "onIceCandidate" :
                JsonNode clientCandidate = jsonMessage.get("candidate");

                if(user != null){
                    //이거에 대한 설명 필요
                    IceCandidate candidate = new IceCandidate(clientCandidate.get("candidate").asText(),
                            clientCandidate.get("sdpMid").asText(),
                            clientCandidate.get("sdpMLineIndex").asInt());
                    //name => username
                    user.addCandidate(candidate, jsonMessage.get("name").asText());
                }
                break;
            default:
                //response service
                break;
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        UserConnection user = userConnectionManager.removeBySession(session);
        //connection service에서 leave 하게 해주세요 (방)
    }


    ClientMessageDto convertMessageToDto(String payload) throws JsonProcessingException {
        return objectMapper.readValue(payload, ClientMessageDto.class);
    }
}
