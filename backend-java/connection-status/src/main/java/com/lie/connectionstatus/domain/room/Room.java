package com.lie.connectionstatus.domain.room;

import com.lie.connectionstatus.domain.Authority;
import com.lie.connectionstatus.domain.User;
import lombok.Builder;
import lombok.Data;
import org.kurento.client.MediaPipeline;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Data
@RedisHash(value = "room")
public class Room {

    @Id
    private String id;
    private ConcurrentMap<String, User> participants = new ConcurrentHashMap<>();
    private MediaPipeline pipeline;
    public Room (){
        this.pipeline = null;
    }
    public Room (MediaPipeline mediaPipeline){
        this.pipeline = mediaPipeline;
    }

    public void join(User participant){
        this.participants.put(participant.getUsername(),
                participant);
    }
}
