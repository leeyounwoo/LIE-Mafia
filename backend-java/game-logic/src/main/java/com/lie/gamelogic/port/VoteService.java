package com.lie.gamelogic.port;

import com.lie.gamelogic.domain.User.Job;
import org.springframework.stereotype.Service;

@Service
public interface VoteService {

    String FinddeadOne(String actionType, String select, Job job);

}
