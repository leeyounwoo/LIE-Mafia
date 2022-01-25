package com.lie.connectionstatus.dto;

import lombok.Data;

@Data
public class ClientMessageDto {
    private String eventType;
    private String actionType;
    private String roomId;
    private String username;
}
