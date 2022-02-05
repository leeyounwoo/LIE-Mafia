package com.lie.connectionstatus.dto;

import com.lie.connectionstatus.domain.room.Room;
import com.lie.connectionstatus.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JoinEventDto {
    private String id;
    private String roomId;
    private User user;
}
