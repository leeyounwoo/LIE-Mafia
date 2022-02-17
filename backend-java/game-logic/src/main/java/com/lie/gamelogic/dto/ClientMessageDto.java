package com.lie.gamelogic.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Data
@AllArgsConstructor
@Log4j2
public class ClientMessageDto {

    private String id;
    private String roomId;
    private String username;
    private String sessionId;

    public ClientMessageDto(JsonNode jsonMessage, ObjectMapper objectMapper){

        this.sessionId = jsonMessage.get("sessionId").asText();

        try {
            JsonNode data = objectMapper.readTree(jsonMessage.get("data").asText());
            log.info(data);
            this.id = data.get("id").asText();
            this.roomId = data.get("roomId").asText();
            this.username = data.get("username").asText();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }


}
