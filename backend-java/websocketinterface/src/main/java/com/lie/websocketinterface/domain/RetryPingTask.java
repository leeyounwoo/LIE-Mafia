package com.lie.websocketinterface.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.websocketinterface.adapter.MessageProducer;
import com.lie.websocketinterface.dto.ClientClosedDataDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.Message;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.TimerTask;

@Data
@Slf4j
public class RetryPingTask extends TimerTask {
    private WebSocketSession clientSession;
    private final SessionManager sessionManager;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String > kafkaProducer;

    public RetryPingTask(SessionManager sessionManager, KafkaTemplate<String, String> kafkaProducer, ObjectMapper objectMapper){
        this.sessionManager = sessionManager;
        this.kafkaProducer = kafkaProducer;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run() {
        PingMessage pingMessage = new PingMessage();
        try{
            sendPing(pingMessage, clientSession);
            return;
        }catch (Exception e){
            log.info("Ping Lost");
        }

        ClientClosedDataDto clientClosedDataDto = new ClientClosedDataDto("leave", clientSession.getId());
        closeClientSession(clientClosedDataDto);

    }
    protected void sendPing(PingMessage pingMessage, WebSocketSession session) throws IOException {
        session.sendMessage(pingMessage);
    }
    private void closeClientSession(ClientClosedDataDto clientClosedDataDto){
        try {
            kafkaProducer.send("connection.leave",objectMapper.writeValueAsString(clientClosedDataDto));
            clientSession.close();
        } catch (JsonProcessingException jsonProcessingException){
            log.debug("Error In making json");
        } catch (IOException ex) {
            log.info("Error In making ");
        }
    }

}
