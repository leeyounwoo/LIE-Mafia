package com.lie.websocketinterface.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.websocketinterface.config.ServiceClient;
import com.lie.websocketinterface.domain.SessionManager;
import com.lie.websocketinterface.dto.ClientClosedDataDto;
import com.lie.websocketinterface.dto.OutboundToServiceMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Qualifier("connectionServiceSession")
    private final WebSocketSession connectionServiceSession;
    @Qualifier("gameServiceSession")
    private final WebSocketSession gameServiceSession;
    private final SessionManager sessionManager;

    public void sendToService(String service, String data, String sessionId) throws ExecutionException, InterruptedException, IOException {
        OutboundToServiceMessageDto outboundToServiceMessageDto = new OutboundToServiceMessageDto(sessionId,data);
        switch (service){
            case "connection":
                connectionServiceSession.sendMessage(new TextMessage(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(outboundToServiceMessageDto)));
                break;
            case "game":
                gameServiceSession.sendMessage(new TextMessage(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(outboundToServiceMessageDto)));
                break;
        }
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
            } catch (IOException e) {
                log.info("PING ERROR");
            } catch (Exception e){
                log.info("UNEXPECTED ERROR");

                sessionManager.removeBySession(session);
                ClientClosedDataDto clientClosedDataDto = new ClientClosedDataDto("leave", session.getId());

                try {
                    sendToService("connection",objectMapper.writeValueAsString(clientClosedDataDto),session.getId());
                    session.close();
                } catch (JsonProcessingException jsonProcessingException){
                    log.info("json parsing error");
                }
                catch (IOException ex) {
                    log.info("error closing session");
                } catch (ExecutionException ex) {
                    log.info("json parsing error");
                } catch (InterruptedException ex) {
                    log.info("interrupt");
                }
            }
        });
        log.info(sessionManager.getSessionBySessionId().size()+" "+"healthy clients left");
    }
}

