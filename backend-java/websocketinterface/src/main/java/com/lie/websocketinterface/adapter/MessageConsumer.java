package com.lie.websocketinterface.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.websocketinterface.config.ServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@Component
@Slf4j
public class MessageConsumer {
    private final ObjectMapper objectMapper;

//    @KafkaListener(topics={"disconnection"}, groupId = "websocket-interface-group")
//    public void serviceDisconnection(String message){
//        log.info(message);
//        try{
//            JsonNode jsonNode = objectMapper.readTree(message);
//            switch (jsonNode.get("service").asText()){
//                case "connection" :
//                    log.info("reconnecting");
//                    //connectionServiceSession = null;
//                    //connectionServiceSession = serviceClient.reconnectToConnectionService();
//                    break;
//                case "game" :
//                    gameServiceSession = serviceClient.connectToGameService();
//                    break;
//
//            }
//        } catch (JsonProcessingException jsonProcessingException){
//            log.info("Error Consuming Service Disconnection ");
//        } catch (ExecutionException e) {
//            log.info("Execution Error ");
//
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            log.info("Interrupt Error");
//
//            e.printStackTrace();
//        }
//
//        log.info(message);
//    }

}
