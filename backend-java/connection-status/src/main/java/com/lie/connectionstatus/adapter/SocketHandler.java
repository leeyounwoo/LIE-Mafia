package com.lie.connectionstatus.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.connectionstatus.domain.user.UserConnection;
import com.lie.connectionstatus.domain.user.UserConnectionManager;
import com.lie.connectionstatus.dto.ClientClosedDataDto;
import com.lie.connectionstatus.dto.EventActionDto;
import com.lie.connectionstatus.port.ConnectionService;
import com.lie.connectionstatus.port.MessageInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.IceCandidate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
@Slf4j
@Component
@RequiredArgsConstructor
public class SocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final MessageInterface messageInterface;
    private final ConnectionService connectionService;
    private final UserConnectionManager userConnectionManager;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        final JsonNode jsonMessage = objectMapper.readTree(message.getPayload());
        final String data = jsonMessage.get("data").toString();
        final EventActionDto eventActionDto = EventActionDto.builder()
                .eventType(jsonMessage.get("eventType").asText())
                .id(jsonMessage.get("data").get("id").asText())
                .build();
        final UserConnection user = userConnectionManager.getBySession(session.getId()); //registry (Redis or ConcurrentMap)

        if(eventActionDto.getEventType().equals("connection")){
            switch(eventActionDto.getId()){
                case "create" :
                    try{
                        connectionService.createRoom(session,
                                jsonMessage.get("data").get("username").asText());
                    }catch (Exception e){
                        e.printStackTrace();
                        break;
                    }
                    break;
                case "join" :
                    connectionService.joinRoom(session,
                            jsonMessage.get("data").get("username").asText(),
                            jsonMessage.get("data").get("roomId").asText());
                    break;
                case "receiveVideoFrom":
                    //sender -> sessionId로 보내면 좋을 듯
                    final String senderName = jsonMessage.get("data").get("sender").asText();
                    //방 사이 닉네임 유효한 부분 있는지 확인 - session으로 바꿔줘야하지 않나 싶음
                    final UserConnection sender = userConnectionManager.getByUsername(senderName);
                    final String sdpOffer = jsonMessage.get("data").get("sdpOffer").asText();
                    user.receiveVideoFrom(sender, sdpOffer);
                    break;

                case "leave" :
                    connectionService.leaveRoom(session);
                    break;

                case "onIceCandidate" :
                    JsonNode clientCandidate = jsonMessage.get("data").get("candidate");

                    if(user != null){
                        //이거에 대한 설명 필요
                        IceCandidate candidate = new IceCandidate(clientCandidate.get("candidate").asText(),
                                clientCandidate.get("sdpMid").asText(),
                                clientCandidate.get("sdpMLineIndex").asInt());
                        //name => username
                        user.addCandidate(candidate, jsonMessage.get("data").get("name").asText());
                    }
                    break;
            }
        }
        else{
            log.info(data);
            messageInterface.sendToService(eventActionDto.createTopic(), data, session.getId());
        }


    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        connectionService.leaveRoom(session);
    }
}
