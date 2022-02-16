package com.lie.gamelogic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReadUserDto {
    String eventType;
    String id;
    String roomId;
    String username;
    boolean ready;
}
