package com.lie.gamelogic.dto;

import com.lie.gamelogic.domain.RoomPhase;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientExecutionVoteDto {

    String id;
    String roomId;
    String username;
    String select;
    Boolean agreeToDead;
    Integer agreeDie;
    Integer agreeAlive;

}
