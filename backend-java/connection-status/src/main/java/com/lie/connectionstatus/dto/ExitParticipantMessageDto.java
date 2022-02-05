package com.lie.connectionstatus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;

@Data
@AllArgsConstructor
public class ExitParticipantMessageDto {
    private String id;
    private String username;
    private String sessionId;
}
