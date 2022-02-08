package com.lie.gamelogic.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.gamelogic.domain.RoomPhase;
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

        switch (jsonMessage.get("id").asText()){
            case "ready":
                gameService.pressReady(session,
                        jsonMessage.get("roomId").asText(),
                        jsonMessage.get("username").asText());
                break;
            case "start":
                gameService.pressStart(session,
                        jsonMessage.get("roomId").asText(),
                        jsonMessage.get("username").asText());
                // 직업 배정 테스트, 메소드 호출
                gameService.roleAssign(jsonMessage.get("roomId").asText());
                //session response

                //createvote 테스트
                gameService.createVote(jsonMessage.get("roomId").asText(), RoomPhase.NIGHT);
                break;
<<<<<<< HEAD
            case "citizenVote":
                gameService.selectVote(session
                        ,jsonMessage.get("roomId").asText()
                        ,jsonMessage.get("username").asText()
                        ,jsonMessage.get("select").asText());
                break;

=======
            case "madeVote":
                if(jsonMessage.hasNonNull("phase") && "citizenVote".equals(jsonMessage.get("phase").asText())){
                    gameService.selectExecutionVote(session
                            ,jsonMessage.get("roomId").asText()
                            ,jsonMessage.get("username").asText()
                            ,jsonMessage.get("select").asText()
                            ,RoomPhase.EXECUTIONVOTE
                            ,jsonMessage.get("agreeToDead").asBoolean()
                    );
                }else {
                    gameService.selectVote(session
                            , jsonMessage.get("roomId").asText()
                            , jsonMessage.get("username").asText()
                            , jsonMessage.get("select").asText()
                    );
                }


                gameService.resultNightVote(jsonMessage.get("roomId").asText());
                break;

            case "delete":
                gameService.deleteVote(jsonMessage.get("roomId").asText());
>>>>>>> c5ea1426c1f17c6add44b349dc4cc5b8421e8b06
        }
        return;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
    }


}
