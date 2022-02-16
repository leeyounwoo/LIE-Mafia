package com.lie.gamelogic.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.gamelogic.domain.RoomPhase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

@AllArgsConstructor
@Data
@Log4j2
public class ClientMessageExceptionVoteDto {
    private String id;
    private String roomId;
    private String roomPhase;
    private String username;
    private String sessionId;
    private String select;
    private Boolean agreeToDead;

    public ClientMessageExceptionVoteDto(JsonNode jsonMessage, ObjectMapper objectMapper){

        this.sessionId = jsonMessage.get("sessionId").asText();

        try {
            JsonNode data = objectMapper.readTree(jsonMessage.get("data").asText());
            log.info(data);

            this.id = data.get("id").asText();
            this.roomId = data.get("roomId").asText();
            this.roomPhase = data.get("phase").asText();
            this.username = data.get("username").asText();
            this.select = data.get("select").asText();
            this.agreeToDead = data.get("agreeToDead").asBoolean();

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }
}
