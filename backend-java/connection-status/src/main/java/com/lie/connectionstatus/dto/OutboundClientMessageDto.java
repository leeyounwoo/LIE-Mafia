package com.lie.connectionstatus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OutboundClientMessageDto {
    private List<String> receivers;
    private String message;
}
