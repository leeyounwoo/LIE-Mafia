package com.lie.gamelogic.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.gamelogic.domain.RoomPhase;
import com.lie.gamelogic.dto.ClientMessageDto;
import com.lie.gamelogic.port.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class GameHandler extends TextWebSocketHandler{
    private final ObjectMapper objectMapper;
    private final GameService gameService;

   @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        final JsonNode jsonMessage = objectMapper.readTree(message.getPayload());

        //session 관리는 이후 api gateway에서 작업할 예정이지만, 테스트를 위해
       ClientMessageDto clientMessageDto = new ClientMessageDto(jsonMessage,objectMapper);
        switch (clientMessageDto.getId()){
            case "ready":
//                gameService.pressReady(session,clientMessageDto.getSessionId(),
//                        clientMessageDto.getRoomId(),
//                        clientMessageDto.getUsername());
                break;
            case "start":
//                gameService.pressStart(clientMessageDto.getSessionId(),
//                        clientMessageDto.getRoomId(),
//                        clientMessageDto.getUsername());
//                break;
            case "madeVote":
//                if(jsonMessage.hasNonNull("phase") && "citizenVote".equals(jsonMessage.get("phase").asText())){
//                    gameService.selectExecutionVote(session
//                            ,jsonMessage.get("roomId").asText()
//                            ,jsonMessage.get("username").asText()
//                            ,jsonMessage.get("select").asText()
//                            , RoomPhase.EXECUTIONVOTE
//                            ,jsonMessage.get("agreeToDead").asBoolean()
//                    );
//                }else {
//                    gameService.selectVote(session
//                            , jsonMessage.get("roomId").asText()
//                            , jsonMessage.get("username").asText()
//                            , jsonMessage.get("select").asText()
//                    );
//                }
            //dead test용
//            case "dead" :
//                gameService.dead(jsonMessage.get("roomId").asText(),jsonMessage.get("username").asText());
//                break;

        }
        return;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
    }


}
