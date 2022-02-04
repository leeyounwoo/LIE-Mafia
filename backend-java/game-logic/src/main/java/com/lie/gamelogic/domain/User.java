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

    public User pressReady(){

        if(this.ready){
            this.ready = false;
            return this;
        }

        this.ready = true;
        return this;
    }
}
