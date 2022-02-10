package com.lie.gamelogic.dto.Start;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
public class startMorngingVoteDto {
    String roomId;
    LocalDateTime endTime;
    List<String> aliveUsers;
    Boolean votable;

}
