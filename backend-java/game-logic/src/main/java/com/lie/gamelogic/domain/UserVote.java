package com.lie.gamelogic.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Data
@AllArgsConstructor
public class UserVote {
    private String username;
    private String sessionId;
    private Job job;
    private String select;



}
