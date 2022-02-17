package com.lie.connectionstatus.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

@Data
@Builder
@Slf4j
@AllArgsConstructor
public class CandidateDto {
    private String candidate;
    private String sdpMid;
    private Integer sdpMLineIndex;

    public CandidateDto(JsonNode candidateTree, ObjectMapper objectMapper){
        try{
            JsonNode candidateNode = objectMapper.readTree(candidateTree.toString());
            log.info("CadidateDto Creator "+ candidateNode.toString());
            if(!ObjectUtils.isEmpty(candidateNode.get("candidate"))){
                this.candidate = candidateNode.get("candidate").asText();
            }
            if(!ObjectUtils.isEmpty(candidateNode.get("sdpMid"))){
                this.sdpMid = candidateNode.get("sdpMid").asText();
            }
            if(!ObjectUtils.isEmpty(candidateNode.get("sdpMLineIndex"))) {
                this.sdpMLineIndex = candidateNode.get("sdpMLineIndex").asInt();
            }
        }catch (JsonProcessingException jsonProcessingException){
            log.info(jsonProcessingException.getMessage());
        }

    }


}
