package com.lie.connectionstatus.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.lie.connectionstatus.domain.User;
import com.lie.connectionstatus.domain.room.Room;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;

@Data
@AllArgsConstructor
public class ExistingParticipantMessageDto {
    private String id;
    private User user;
    private Room data;
}
