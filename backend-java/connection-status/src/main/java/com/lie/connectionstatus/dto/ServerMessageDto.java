package com.lie.connectionstatus.dto;

import com.lie.connectionstatus.domain.room.Room;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServerMessageDto {
    private String id;
    private Room data;
}
