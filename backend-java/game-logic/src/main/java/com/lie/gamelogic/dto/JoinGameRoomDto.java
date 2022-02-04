package com.lie.gamelogic.dto;

import com.lie.gamelogic.domain.User;
import lombok.Data;

@Data
public class JoinGameRoomDto {
    private String id;
    private String roomId;
    private User user;
}
