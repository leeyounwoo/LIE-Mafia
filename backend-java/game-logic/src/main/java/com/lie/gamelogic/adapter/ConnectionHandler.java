package com.lie.gamelogic.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.gamelogic.dto.Client.ClientMessageDto;
import com.lie.gamelogic.dto.Client.VoteDto;
import com.lie.gamelogic.dto.Client.executionVoteDto;
import com.lie.gamelogic.port.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectionHandler extends TextWebSocketHandler {
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final GameService gameService;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info(message.toString());
        log.info("Socket HI");
        objectMapper.createParser(message.getPayload());

        ClientMessageDto incomingMessage = convertMessageToDto(message.getPayload());

        log.info(incomingMessage.toString());

        switch (incomingMessage.getActionType()){
            case "ready" : gameService.GameReady(incomingMessage.getRoomId(),incomingMessage.getUsername());break;
            case "start" : gameService.GameStart(session, incomingMessage.getRoomId());
            break;
            case "executionVote" :
                executionVoteDto incomingMessage1 = convertExMessageToDto(message.getPayload());
                gameService.executionVote(incomingMessage1.getRoomId(),incomingMessage1.getSelect(),incomingMessage1.isAgreeToDead());
            break;
            default:
                VoteDto incomingMessage2 = convertVoteToDto(message.getPayload());
                gameService.findVote(incomingMessage2);
                break;

        }

    }

    private ClientMessageDto convertMessageToDto(String payload) throws JsonProcessingException {
        return objectMapper.readValue(payload, ClientMessageDto.class);
    }

    private executionVoteDto convertExMessageToDto(String payload) throws JsonProcessingException {
        return objectMapper.readValue(payload, executionVoteDto.class);
    }

    private VoteDto convertVoteToDto(String payload) throws JsonProcessingException {
        return objectMapper.readValue(payload, VoteDto.class);
    }
}
