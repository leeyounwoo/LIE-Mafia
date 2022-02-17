package com.lie.websocketinterface.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.websocketinterface.config.ServiceClient;
import com.lie.websocketinterface.domain.PingTimer;
import com.lie.websocketinterface.domain.RetryPingTask;
import com.lie.websocketinterface.domain.SessionManager;
import com.lie.websocketinterface.dto.ClientClosedDataDto;
import com.lie.websocketinterface.dto.InboundMessageDto;
import com.lie.websocketinterface.dto.OutboundToServiceMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
@Component
@RequiredArgsConstructor
@Slf4j
public class MessageProducer {
    private final ObjectMapper objectMapper;
    private final SessionManager sessionManager;
    private final KafkaTemplate<String, String> kafkaProducer;
    public void publishOnKafka(String topic, String message){
        kafkaProducer.send(topic, message);
    }

    public void sendToParticipants(List<WebSocketSession> participants, String message){
        participants.stream().forEach(participant -> {
            try {
                participant.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.info("Sending Message To Session is Not Available");
            }
        });
    }
    @Scheduled(fixedDelay=40000)
    private void sendPingMessageToClients(){
        PingMessage pingMessage = new PingMessage();
        sessionManager.getSessionBySessionId().values().stream().forEach(session -> {
            try{
                session.sendMessage(pingMessage);
                log.info(sessionManager.getSessionBySessionId().size()+" "+"healthy clients left");
                return;
            } catch (IOException e) {
                log.info("PING MESSAGE OUT ERROR");
            } catch (Exception e) {
                log.info("UNEXPECTED ERROR");

                PingTimer pingTimer = new PingTimer();
                RetryPingTask retryPing = new RetryPingTask(sessionManager, kafkaProducer, objectMapper);

                retryPing.setClientSession(session);
                pingTimer.schedule(retryPing, 40000);
            }
        });
    }
}

