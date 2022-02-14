package com.lie.websocketinterface.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class InboundMessageDto {
    private String eventType;
    private String data;
}
