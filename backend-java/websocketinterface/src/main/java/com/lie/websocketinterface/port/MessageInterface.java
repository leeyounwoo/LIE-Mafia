package com.lie.websocketinterface.port;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.lie.websocketinterface.adapter.MessageProducer;
import com.lie.websocketinterface.dto.OutboundMessageDto;
import com.lie.websocketinterface.dto.OutboundToServiceMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
@Component
@RequiredArgsConstructor
public class MessageInterface {
    private final MessageProducer messageProducer;
    private final ObjectMapper objectMapper;

    public void sendToService(String topic, String data, String sessionId) throws IOException {
        OutboundToServiceMessageDto outboundToServiceMessageDto = new OutboundToServiceMessageDto(sessionId,data);
        messageProducer.publishOnKafka(topic, objectMapper.writeValueAsString(outboundToServiceMessageDto));
    }

}
