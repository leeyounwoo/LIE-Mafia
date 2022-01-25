package com.lie.connectionstatus.domain.room;

import com.lie.connectionstatus.domain.Authority;
import com.lie.connectionstatus.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kurento.client.MediaPipeline;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Data
@RedisHash(value = "room")
public class Room {

    @Id
    @Indexed
    private String id;
    private HashMap<String, User> participants = new HashMap<>();
    private RoomStatus roomStatus;
    public Room (){
       this.roomStatus = RoomStatus.WAIT;
    }

    public Boolean checkIfUserExists(String username){
        if(ObjectUtils.isEmpty(participants.get(username))){
            return false;
        }
        return true;
    }

    public void join(User participant){
        this.participants.put(participant.getUsername(), participant);
    }
}
