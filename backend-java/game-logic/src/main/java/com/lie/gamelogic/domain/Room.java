package com.lie.gamelogic.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.stereotype.Indexed;

import java.time.LocalDateTime;
import java.util.HashMap;

@Data
@RedisHash(value = "room")
public class Room {
    @Id
    String roomId;
    HashMap<String,User> participants = new HashMap<>();
    RoomStatus roomStatus;
    RoomPhase roomPhase;
    Integer day;
    LocalDateTime endTime;

}
