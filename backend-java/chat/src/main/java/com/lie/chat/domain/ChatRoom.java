package com.lie.chat.domain;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import java.util.HashMap;

@Data
@RedisHash(value = "room")
public class ChatRoom {
    @Id
    String roomId;
    HashMap<String,User> participants = new HashMap<>();
    RoomStatus roomStatus;

}
