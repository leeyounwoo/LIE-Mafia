package com.lie.gamelogic.domain;

import lombok.Data;

@Data
public class User {

    private String username;
    private String sessionId;
    private Boolean ready;
    private Authority authority;
    private Job job;
    private Boolean alive;

}
