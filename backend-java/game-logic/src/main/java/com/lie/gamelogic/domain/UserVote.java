package com.lie.gamelogic.domain;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class UserVote {
    private String username;
    private String sessionId;
    private Job job;
    private String select;
}
