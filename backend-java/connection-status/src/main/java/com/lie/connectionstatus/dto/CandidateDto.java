package com.lie.connectionstatus.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CandidateDto {
    private String candidate;
    private String sdpMid;
    private Integer sdpMLineIndex;


    public CandidateDto buildDto(JsonNode candidateTree, ObjectMapper objectMapper) throws JsonProcessingException {
        JsonNode candidateNode = objectMapper.readTree(candidateTree.asText());
        this.candidate = candidateNode.get("candidate").asText();
        this.sdpMid = candidateNode.get("sdpMid").asText();
        this.sdpMLineIndex = candidateNode.get("sdpMLineIndex").asInt();
        return this;
    }
}
