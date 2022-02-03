package com.lie.gamelogic.dto.Client;

import lombok.Data;

@Data
public class ClientMessageDto {

    private String eventType;

    private String actionType;

    private String roomId;

    private String username;

}
