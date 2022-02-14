package com.lie.connectionstatus.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.connectionstatus.domain.user.UserConnectionManager;
import com.lie.connectionstatus.dto.ClientClosedDataDto;
import com.lie.connectionstatus.port.ConnectionService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.TimerTask;

@Data
@Slf4j
public class RetryPingTask extends TimerTask {
    private WebSocketSession clientSession;
    private final UserConnectionManager userConnectionManager;
    private final ObjectMapper objectMapper;
    private final ConnectionService connectionService;

    public RetryPingTask(UserConnectionManager userConnectionManager,  ObjectMapper objectMapper, ConnectionService connectionService){
        this.userConnectionManager = userConnectionManager;
        this.objectMappe}

    @Override
    public void run() {
        PingMessage pingMessage = new PingMessage();
        log.info("client Session" + clientSession.getId());
        try{
            sendPing(pingMessage, clientSession);
            return;
        }catch (Exception e){
            log.info("Ping Lost");
        }
    }
    protected void sendPing(PingMessage pingMessage, WebSocketSession session) throws IOException {
        session.sendMessage(pingMessage);
    }
    private void closeClientSession(ClientClosedDataDto clientClosedDataDto){
        try {
            clientSession.close();
        } catch (JsonProcessingException jsonProcessingException){
            log.debug("Error In making json");
        } catch (IOException ex) {
            log.info("Error In making ");
        }
    }

}
