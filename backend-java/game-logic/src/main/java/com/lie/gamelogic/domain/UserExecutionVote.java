package com.lie.gamelogic.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
public class UserExecutionVote {
    private String username;
    private String sessionId;
    private String select;
    private Boolean agreeToDead;
    private Boolean voted;
}
