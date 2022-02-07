package com.lie.gamelogic.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
                gameService.createVote(jsonMessage.get("roomId").asText());
                break;
            case "madeVote":
                gameService.selectVote(session
                        ,jsonMessage.get("roomId").asText()
                        ,jsonMessage.get("username").asText()
                        ,jsonMessage.get("select").asText());

                gameService.resultMornigVote(jsonMessage.get("roomId").asText());
                break;
        }
        return;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
    }


}
