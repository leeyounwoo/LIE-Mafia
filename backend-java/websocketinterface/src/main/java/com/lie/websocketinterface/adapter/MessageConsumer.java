package com.lie.websocketinterface.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.lie.websocketinterface.config.ServiceClient;
import com.lie.websocketinterface.domain.SessionManager;
import com.lie.websocketinterface.dto.OutboundErrorDto;
import com.lie.websocketinterface.dto.OutboundMessageDto;
import com.lie.websocketinterface.port.MessageInterface;
import com.lie.websocketinterface.port.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@Component
@Slf4j
public class MessageConsumer {
    private final ObjectMapper objectMapper;
    private final SessionService sessionService;

    @KafkaListener(topics={"client.response"}, groupId = "websocket-interface-test-group")
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
    @KafkaListener(topics={"error"}, groupId = "websocket-interface-test-group")
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
}
