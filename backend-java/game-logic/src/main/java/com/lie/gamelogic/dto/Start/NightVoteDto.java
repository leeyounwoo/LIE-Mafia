package com.lie.gamelogic.dto.Start;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class NightVoteDto {

    String roomId;
    LocalDateTime endTime;
    List<String> aliveUsers;
    boolean votable;
    List<String> coworker;
}
