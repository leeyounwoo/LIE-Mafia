package com.lie.connectionstatus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.util.internal.ObjectUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.util.Map;

@Data
@Slf4j
@ApiModel(description = "클라이언트에서 전달하는 message convention 바탕의 DTO 입니다")
@Builder
@AllArgsConstructor
public class ClientMessageDto {
    private String id;
    private String roomId;
    private String username;
    private String sessionId;
    private String sender;
    private String sdpOffer;
    private CandidateDto candidate;
    private String name;

    public ClientMessageDto(JsonNode jsonMessage,ObjectMapper objectMapper){
        this.sessionId = jsonMessage.get("sessionId").asText();
        try{
            JsonNode data = objectMapper.readTree(jsonMessage.get("data").asText());
            this.id = data.get("id").asText();
            if(!ObjectUtils.isEmpty(data.get("username"))){
                this.username = data.get("username").asText();
            }
            if(!ObjectUtils.isEmpty(data.get("roomId"))){
                this.roomId = data.get("roomId").asText();
            }
            if(!ObjectUtils.isEmpty(data.get("sender"))){
                this.sender = data.get("sender").asText();
            }
            if(!ObjectUtils.isEmpty(data.get("sdpOffer"))){
                this.sdpOffer = data.get("sdpOffer").asText();
            }
            if(!ObjectUtils.isEmpty(data.get("name"))){
                this.name = data.get("name").asText();
            }
            if(!ObjectUtils.isEmpty(data.get("candidate"))){
                log.info(data.get("candidate").toString());
                log.info("candidate not null");
                this.candidate = new CandidateDto(data.get("candidate"),objectMapper);
            }

        } catch (JsonMappingException e) {
            log.info("Json Mapping Exception when processing \"data\"");
            return;
        } catch (JsonProcessingException e) {
            log.info("Json Processing Exception when processing \"data\"");
            return;
        } catch (Exception e){
            log.info("error");
            return;
        }
    }

}
