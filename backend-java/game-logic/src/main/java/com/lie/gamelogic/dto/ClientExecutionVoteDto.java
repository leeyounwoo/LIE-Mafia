package com.lie.gamelogic.dto;

import com.lie.gamelogic.domain.RoomPhase;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientExecutionVoteDto {
    String eventType;
    String id;
    RoomPhase phase;
    String roomId;
    String username;
    String select;
    Boolean agreeToDead;
    Integer agreeDie;
    Integer agreeAlive;

}
