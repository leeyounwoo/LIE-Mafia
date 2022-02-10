package com.lie.websocketinterface.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboundMessageDto {
    private List<String> receivers;
    private String message;

}
