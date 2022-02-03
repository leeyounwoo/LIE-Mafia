package com.lie.gamelogic.port;

import com.lie.gamelogic.domain.User.Job;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VoteServiceImpl implements VoteService{

    @Override
    public String FinddeadOne(String actionType, String select, Job job) {

        if(actionType == "citizenVote"){

        }

        return null;
    }
}
