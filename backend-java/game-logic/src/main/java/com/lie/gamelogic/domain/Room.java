package com.lie.gamelogic.domain;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.stereotype.Indexed;

import java.time.LocalDateTime;
import java.util.HashMap;

@Slf4j
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

    public Room join(User user){
        this.participants.put(user.getUsername(), user);
        return this;
    }
    public Room leave(String username){
        this.participants.remove(username);
        return this;
    }

    public void close(){
        log.info("Room {} : closing", this.getRoomId());
        this.participants.clear();
    }
    public Room pressStart(String username){

        if(!checkIfUserIsLeader(username)){
            return null;
        }
        if(participants.size()<3){
            return null;
        }
//        for(User participant : participants.values()){
//            if(participant.getUsername().equals(username)){
//                continue;
//            }
//            if(!participant.getReady()){
//                return null;
//            }
//        }

        this.roomStatus = RoomStatus.START;

        //start 후 로직은 추가적으로 짜주세요
        this.roomPhase = RoomPhase.MORNING;
        this.day = 1;

        return this;
    }
    public Room pressReady(String username){
        User user = this.participants.get(username);
        user.pressReady();
        this.participants.put(username, user);
        return this;
    }

    public Boolean checkIfUserIsLeader(String username){
        if(participants.get(username).getAuthority().equals(Authority.LEADER)){
            return true;
        }
        return false;
    }
    //방 id 내에 username 이 존재하는가
    public Boolean checkIfUserExists(String username){
        if(participants.containsKey(username)){
            return true;
        }
        return false;
    }
    public User getUserByUsername(String username){
        if(checkIfUserExists(username)){
            return this.participants.get(username);
        }
        return null;
    }
}
