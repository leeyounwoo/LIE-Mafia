package com.lie.websocketinterface.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class OutboundErrorDto {
    private String sessionId;
    private String message;
}
