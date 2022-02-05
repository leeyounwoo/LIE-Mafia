package com.lie.connectionstatus.dto;

import com.lie.connectionstatus.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;

@Data
@AllArgsConstructor
public class ExitParticipantMessageDto {
    private String id;
    private String roomId;
    private String username;
}
