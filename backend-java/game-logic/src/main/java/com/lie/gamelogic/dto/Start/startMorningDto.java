package com.lie.gamelogic.dto.Start;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class startMorningDto {

    String roomId;
    LocalDateTime EndTime;
    List<String> aliveUsers;
    Integer dayCount;
    Boolean isDay;
}
