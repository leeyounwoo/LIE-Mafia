package com.lie.gamelogic.dto.Client;

import com.lie.gamelogic.domain.User.Job;
import lombok.Data;

@Data
public class VoteDto extends ClientMessageDto{

    String select;
    Job job;

}
