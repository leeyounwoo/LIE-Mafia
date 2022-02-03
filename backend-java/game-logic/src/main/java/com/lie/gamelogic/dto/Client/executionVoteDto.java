package com.lie.gamelogic.dto.Client;

import lombok.Data;

@Data
public class executionVoteDto extends ClientMessageDto{

    String select;
    boolean agreeToDead;

}
