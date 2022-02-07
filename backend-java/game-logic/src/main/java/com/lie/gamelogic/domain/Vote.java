package com.lie.gamelogic.domain;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.HashMap;

@Slf4j
@Data
@RedisHash(value = "vote")
public class Vote {

    @Id
    String voteId;
    String roomId;
    RoomPhase roomPhase;
    HashMap<String,UserVote> votes=new HashMap<String, UserVote>();;


    public Vote createVote(String roomId,RoomPhase roomPhase){
        this.roomId=roomId;
        this.voteId="vote"+roomId;
        this.roomPhase=roomPhase;
        return this;
    }

    public void putUserVote(String username,UserVote userVote) {
        this.votes.put(username,userVote);
    }
}
