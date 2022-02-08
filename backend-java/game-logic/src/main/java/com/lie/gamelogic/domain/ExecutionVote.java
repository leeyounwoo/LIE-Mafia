package com.lie.gamelogic.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.HashMap;

@Slf4j
@Data
@RedisHash(value = "executionvote")
public class ExecutionVote {
    @Id
    String voteId;
    String roomId;
    RoomPhase roomPhase;
    HashMap<String,UserExecutionVote> votes=new HashMap<String, UserExecutionVote>();
    Integer agreeDie;
    Integer agreeAlive;

    public ExecutionVote createVote(String roomId, RoomPhase phase) {
        this.roomId=roomId;
        this.voteId="executionvote"+roomId;
        this.roomPhase=phase;
        agreeDie=0;
        agreeAlive=0;
        return this;
    }

    public void putUserVote(String username, UserExecutionVote userVote) {
        this.votes.put(username,userVote);
    }

    public ExecutionVote pressVoted(String username,Boolean agree){
        UserExecutionVote vote=votes.get(username);
        if(vote.getVoted()&&vote.getAgreeToDead()!=agree){
            if(agree) {
                if(agreeAlive>0) this.agreeAlive--;
                this.agreeDie++;
            }else {
                this.agreeAlive++;
                if(agreeDie>0) this.agreeDie--;
            }
        }else if(!vote.getVoted()){
            if(agree) {
                this.agreeDie++;
            }else {
                this.agreeAlive++;
            }
        }

        vote.setVoted(true); //투표처리리
        vote.setAgreeToDead(agree);
        putUserVote(username,vote);

        return this;
    }
}
