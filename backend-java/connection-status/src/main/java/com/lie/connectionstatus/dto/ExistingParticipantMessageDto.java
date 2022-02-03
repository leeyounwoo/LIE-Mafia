package com.lie.connectionstatus.dto;

import com.lie.connectionstatus.domain.user.User;
import com.lie.connectionstatus.domain.room.Room;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExistingParticipantMessageDto {
    private String id;
    private User user;
    private Room data;
}
