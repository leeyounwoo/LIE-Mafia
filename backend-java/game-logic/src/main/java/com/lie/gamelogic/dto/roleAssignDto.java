package com.lie.gamelogic.dto;

import com.lie.gamelogic.domain.Job;
import com.lie.gamelogic.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class roleAssignDto {
    String eventType;
    String id;
    String roomId;
    List<String> players;
    LocalDateTime EndTime;
    Job job;
}
