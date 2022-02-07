package com.lie.gamelogic.domain;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Data
@RedisHash(value = "vote")
public class Vote {

    @Id
    String roomId;
    RoomPhase roomPhase;
    HashMap<String,UserVote> votes;

    public Vote createVote(String roomId,RoomPhase roomPhase){
        this.roomId=roomId;
        this.roomPhase=roomPhase;
        votes=new HashMap<String, UserVote>();
        return this;
    }

    public void putUserVote(String username,UserVote userVote) {
        this.votes.put(username,userVote);
    }

    public List<String> selectList(){
        List<String> selectList=new ArrayList<>();
        for(UserVote userVote:votes.values()){
            selectList.add(userVote.getSelect());
        }
        return selectList;
    }


}
